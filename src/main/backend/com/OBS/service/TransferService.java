package com.OBS.service;

import com.OBS.alternativeBodies.ClientCreditWorthiness;
import com.OBS.alternativeBodies.KeyValueObject;
import com.OBS.alternativeBodies.ValueAndPercent;
import com.OBS.entity.Client;
import com.OBS.entity.Loan;
import com.OBS.entity.Transfer;
import com.OBS.entity.Visit;
import com.OBS.enums.SearchOperation;
import com.OBS.enums.TransferCategory;
import com.OBS.repository.TransferRepository;
import com.OBS.searchers.SearchCriteria;
import com.OBS.searchers.specificators.Specifications;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.OBS.enums.TransferCategory.BILLS;
import static com.OBS.enums.TransferType.INCOMING;
import static com.OBS.enums.TransferType.OUTGOING;

@Service
@AllArgsConstructor
public class TransferService {
    private final TransferRepository transferRepository;
    private final ClientService clientService;

    public List<Transfer> getTransfers() {
        return transferRepository.findAll();
    }

    public List<Transfer> getRecentTransfers(Long client_id) {
        return transferRepository.findRecentTransfersByClient_clientIdOrderByTransferDateDesc(client_id, PageRequest.of(0, 3));
    }

    public List<Transfer> getTransfers(Specification<Transfer> filterTransferSpec) {
        return transferRepository.findAll(filterTransferSpec,Sort.by(Sort.Direction.DESC,"transferDate"));
    }

    public void addTransfer(Transfer transfer) {
        transferRepository.save(transfer);
    }

    public void deleteTransfer(Long transferId) {
        if (!transferRepository.existsById(transferId))
            throw new IllegalStateException("Transfer with given id " + transferId + " doesn't exists");
        transferRepository.deleteById(transferId);
    }

    @Transactional
    public void performTransfer(Client client, Loan clientLoan) {
        if (client == null) throw new IllegalStateException("Internal Server Error, Sender not available");
        if (client.getBalance() < clientLoan.getRateAmount()) {
            throw new IllegalStateException("There is insufficient account balance to perform this transaction");
        }
        updateBalance(client, clientLoan);
    }

    private void updateBalance(Client client, Loan clientLoan) {
        clientService.updateClientBalance(client, clientLoan.getRateAmount(), OUTGOING.name());
        Transfer clientRateTransfer = new Transfer(
                clientLoan.getRateAmount(),
                LocalDateTime.now(),
                BILLS.getCategory(),
                OUTGOING.name(),
                "Future Bank Sp. z o.o.",
                "Loan: " + clientLoan.getLoanId() + " | Rate number:" + (clientLoan.getNumOfRates() - clientLoan.getRatesLeftToPay() + 1),
                "Restricted Account Number"
        );

        clientRateTransfer.setClient(client);
        addTransfer(clientRateTransfer);
    }


    @Transactional
    public void performTransfer(Transfer transfer) {
        Client sender = clientService.getClientOrNull(transfer.getClient().getClientId());
        Client receiver = clientService.getClientByAccountNumber(transfer.getToAccountNumber());

        if (sender == null) throw new IllegalStateException("Internal Server Error, Sender not available");
        if (sender.getBalance() < transfer.getAmount()) {
            throw new IllegalStateException("Account balance is insufficient to perform this transaction");
        }

        updateBalances(sender, receiver, transfer);
    }

    @Transactional
    public void performLoanTransfer(Transfer transfer) {
        updateBalances(transfer.getClient(), transfer);
    }

    private void updateBalances(Client receiver, Transfer transfer) {
        if (!(receiver == null)) {
            clientService.updateClientBalance(receiver, transfer.getAmount(), INCOMING.name());
            Transfer receiverTransfer = new Transfer(
                    transfer.getAmount(),
                    transfer.getTransferDate(),
                    transfer.getCategory(),
                    INCOMING.name(),
                    "Future Bank Sp. z o.o.",
                    transfer.getTitle(),
                    receiver.getAccountNumber()
            );
            receiverTransfer.setClient(receiver);
            addTransfer(receiverTransfer);
        }
    }

    private void updateBalances(Client sender, Client receiver, Transfer transfer) {
        clientService.updateClientBalance(sender, transfer.getAmount(), OUTGOING.name());
        String receiverName;
        if (receiver != null)
            receiverName = receiver.getFullName();
        else receiverName = transfer.getReceiver_sender();
        Transfer senderTransfer = new Transfer(
                transfer.getAmount(),
                transfer.getTransferDate(),
                transfer.getCategory(),
                OUTGOING.name(),
                receiverName,
                transfer.getTitle(),
                transfer.getToAccountNumber()
        );
        senderTransfer.setClient(sender);
        addTransfer(senderTransfer);

        if (!(receiver == null)) {
            clientService.updateClientBalance(receiver, transfer.getAmount(), INCOMING.name());
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

    public ClientCreditWorthiness getClientWorthiness(Long clientId,int months){
        ClientCreditWorthiness clientCreditWorthiness = new ClientCreditWorthiness();

        Specifications<Transfer> findAllByClientAndWithinMonths = new Specifications<Transfer>()
                .add(new SearchCriteria("client", clientService.getClient(clientId), SearchOperation.EQUAL))
                .add(new SearchCriteria(
                        "transferDate",
                        LocalDateTime.now().minusMonths(months),
                        SearchOperation.GREATER_THAN_DATE));

        Specifications<Transfer> findAllByOutgoing = findAllByClientAndWithinMonths.clone()
                .add(new SearchCriteria("type", OUTGOING.toString(), SearchOperation.EQUAL));

        Specifications<Transfer> findAllByIncoming = findAllByClientAndWithinMonths.clone()
                .add(new SearchCriteria("type", INCOMING.toString(), SearchOperation.EQUAL));

        clientCreditWorthiness.setSumOfOutgoing(getTotalAmount(transferRepository.findAll(findAllByOutgoing)));
        clientCreditWorthiness.setSumOfIncoming(getTotalAmount(transferRepository.findAll(findAllByIncoming)));

        clientCreditWorthiness.setSumOfBalance(clientCreditWorthiness.getSumOfIncoming() - clientCreditWorthiness.getSumOfOutgoing());

        clientCreditWorthiness.setMonthlyBalance(clientCreditWorthiness.getSumOfBalance() / months);

        return clientCreditWorthiness;
    }

    public List<KeyValueObject<String, ValueAndPercent>> getClientHistory(Long clientId,int months) {
        ArrayList<KeyValueObject<String, ValueAndPercent>> clientHistory = new ArrayList<>();

        Specifications<Transfer> findAllByClientAndWithinMonths = new Specifications<Transfer>()
                .add(new SearchCriteria("client", clientService.getClient(clientId), SearchOperation.EQUAL))
                .add(new SearchCriteria(
                        "transferDate",
                        LocalDateTime.now().minusMonths(months),
                        SearchOperation.GREATER_THAN_DATE));

        Specifications<Transfer> findAllByOutgoing = findAllByClientAndWithinMonths.clone()
                .add(new SearchCriteria("type", OUTGOING.toString(), SearchOperation.EQUAL));

        Specifications<Transfer> findAllByIncoming = findAllByClientAndWithinMonths.clone()
                .add(new SearchCriteria("type", INCOMING.toString(), SearchOperation.EQUAL));

        float sumOfOutgoing = getTotalAmount(transferRepository.findAll(findAllByOutgoing));
        float sumOfIncoming = getTotalAmount(transferRepository.findAll(findAllByIncoming));

        clientHistory.add(new KeyValueObject<>("Suma Wydatków", new ValueAndPercent(sumOfOutgoing,sumOfOutgoing / (sumOfIncoming + sumOfOutgoing) * 100)));
        clientHistory.add(new KeyValueObject<>("Suma Przychodów", new ValueAndPercent(sumOfIncoming,sumOfIncoming / (sumOfIncoming + sumOfOutgoing) * 100)));

        for (TransferCategory category : TransferCategory.values()) {
            Specifications<Transfer> findAllByCategory = findAllByOutgoing.clone()
                    .add(new SearchCriteria("category", category.getCategory(), SearchOperation.EQUAL));

            float sumFromCategory = getTotalAmount(transferRepository.findAll(findAllByCategory));

            clientHistory.add(new KeyValueObject<>(
                    category.getCategory(),
                    new ValueAndPercent(
                            sumFromCategory,
                            sumFromCategory / sumOfOutgoing  * 100
                    ))
            );
        }
        return clientHistory;
    }

    private Float getTotalAmount(List<Transfer> transfers) {
        float sum = 0f;
        for (Transfer transfer : transfers)
            sum += transfer.getAmount();

        return sum;
    }

    public List<Transfer> getTransfersBySpecification(Specifications<Transfer> visitSpecifications) {
        return transferRepository.findAll(visitSpecifications);
    }
}
