package com.OBS.service;

import com.OBS.entity.Client;
import com.OBS.entity.Loan;
import com.OBS.entity.Transfer;
import com.OBS.repository.TransferRepository;
import com.OBS.requestBodies.FilterTransferFromClient;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.OBS.enums.TransferType.INCOMING;
import static com.OBS.enums.TransferType.OUTGOING;
import static com.OBS.enums.TransferCategory.*;

@Service
@AllArgsConstructor
public class TransferService {
    private final TransferRepository transferRepository;
    private final ClientService clientService;


    public List<Transfer> getTransfers() {
        return transferRepository.findAll();
    }

    public List<Transfer> getTransfers(Long client_id, FilterTransferFromClient body) {
        List<Transfer> transfers = transferRepository.findAllByClient_clientId(client_id);
        transfers.removeIf(t ->
            t.getTransferDate().isBefore(body.getDateFrom())
                    || t.getTransferDate().isAfter(body.getDateTo())
                || !t.getType().equals(body.getTransferType())
                || !t.getCategory().equals(body.getTransferCategory())
        );
        return transfers;
    }

    public void addTransfer(Transfer transfer) {
        transferRepository.save(transfer);
    }


    public void deleteTransfer(Long transferId) {

        if(!transferRepository.existsById(transferId))
            throw new IllegalStateException("Transfer with given id " + transferId + " doesn't exists");
        transferRepository.deleteById(transferId);
    }

    @Transactional
    public void performTransfer(Client client, Loan clientLoan){
        if(client == null) throw new IllegalStateException("Internal Server Error, Sender not available");
        if(client.getBalance() < clientLoan.getRateAmount()){
            throw new IllegalStateException("There is insufficient account balance to perform this transaction");
        }
        updateBalance(client,clientLoan);
    }

    private void updateBalance(Client client,Loan clientLoan){
        clientService.updateClientBalance(client, clientLoan.getRateAmount(), OUTGOING.name());
        Transfer clientRateTransfer = new Transfer(
                clientLoan.getRateAmount(),
                LocalDateTime.now(),
                BILLS.name(),
                OUTGOING.name(),
                client.getFullName(),
                "Loan: "+ clientLoan.getLoanId() +" | Rate number:" + (clientLoan.getNumOfRates() - clientLoan.getRatesLeftToPay() + 1),
                "Restricted Account Number"
        );

        clientRateTransfer.setClient(client);
        addTransfer(clientRateTransfer);
    }


    @Transactional
    public void performTransfer(Transfer transfer){
        Client sender = clientService.getClientOrNull(transfer.getClient().getClientId());
        Client receiver = clientService.getClientByAccountNumber(transfer.getToAccountNumber());

        if(sender == null) throw new IllegalStateException("Internal Server Error, Sender not available");
        if(sender.getBalance() < transfer.getAmount()){
            throw new IllegalStateException("Account balance is insufficient to perform this transaction");
        }

        updateBalances(sender,receiver,transfer);
    }

    private void updateBalances(Client sender,Client receiver,Transfer transfer){
        clientService.updateClientBalance(sender,transfer.getAmount(),OUTGOING.name());
        Transfer senderTransfer = new Transfer(
                transfer.getAmount(),
                transfer.getTransferDate(),
                transfer.getCategory(),
                OUTGOING.name(),
                sender.getFullName(),
                transfer.getTitle(),
                transfer.getToAccountNumber()
        );
        senderTransfer.setClient(sender);
        addTransfer(senderTransfer);

        if(!(receiver == null)){
            clientService.updateClientBalance(receiver,transfer.getAmount(),INCOMING.name());
            Transfer receiverTransfer = new Transfer(
                    transfer.getAmount(),
                    transfer.getTransferDate(),
                    transfer.getCategory(),
                    INCOMING.name(),
                    sender.getFullName(),
                    transfer.getTitle(),
                    receiver.getAccountNumber()
            );
            receiverTransfer.setClient(receiver);
            addTransfer(receiverTransfer);
        }
    }

    public List<Transfer> getRecentTransfers(Long client_id) {
        return transferRepository.findRecentTransfersByClient_clientIdOrderByTransferDateDesc(client_id, PageRequest.of(0, 3));
    }
}
