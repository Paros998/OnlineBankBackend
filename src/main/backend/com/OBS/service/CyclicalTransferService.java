package com.OBS.service;

import com.OBS.email.EmailService;
import com.OBS.email.EmailTemplates;
import com.OBS.entity.Client;
import com.OBS.entity.CyclicalTransfer;
import com.OBS.entity.Transfer;
import com.OBS.repository.CyclicalTransferRepository;


import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import static com.OBS.enums.TransferType.*;

@Service
@AllArgsConstructor
public class CyclicalTransferService {
    private final CyclicalTransferRepository cyclicalTransferRepository;
    private final EmailService emailService;
    private final TransferService transferService;
    private final ClientService clientService;
    private final EmailTemplates emailTemplates;

    private String TransferNotExists(Long transferId) { return "Cyclical Transfer with given id "+ transferId + " is not present in database" ;}

    public List<CyclicalTransfer> getTransfers() {
        return cyclicalTransferRepository.findAll();
    }

    public CyclicalTransfer getTransfer(Long transferId) {
        return cyclicalTransferRepository.findById(transferId).orElseThrow(
                () -> new IllegalStateException(TransferNotExists(transferId))
        );
    }

    public List<CyclicalTransfer> getClientTransfers(Long clientId) {
        return cyclicalTransferRepository.findAllByClient_clientId(clientId);
    }

    public void addTransfer(CyclicalTransfer cyclicalTransfer) {
        checkForDuplicatedTransfers(cyclicalTransfer);

        cyclicalTransferRepository.save(cyclicalTransfer);
    }

    private void checkForDuplicatedTransfers(CyclicalTransfer cyclicalTransfer) {
        List<CyclicalTransfer> clientsTransfers = getClientTransfers(cyclicalTransfer.getClient().getClientId());

        for(CyclicalTransfer transfer : clientsTransfers){
            if(
                    Objects.equals(transfer.getAmount(), cyclicalTransfer.getAmount())
                    && Objects.equals(transfer.getReTransferDate(), cyclicalTransfer.getReTransferDate())
                    && Objects.equals(transfer.getCategory(), cyclicalTransfer.getCategory())
                    && Objects.equals(transfer.getAccountNumber(), cyclicalTransfer.getAccountNumber())
                    && Objects.equals(transfer.getTitle(), cyclicalTransfer.getTitle())
            )
                throw new IllegalStateException("This exact cyclical transfer is already declared");
        }
    }

    @Transactional
    public void updateTransfer(CyclicalTransfer cyclicalTransfer, Long transferId) {
        if(!cyclicalTransferRepository.existsById(transferId))
            throw new IllegalStateException(TransferNotExists(transferId));

        checkForDuplicatedTransfers(cyclicalTransfer);

        // TODO check if this works and change if it's not
        cyclicalTransfer.setTransferId(transferId);

        cyclicalTransferRepository.save(cyclicalTransfer);
    }

    public void deleteTransfer(Long transferId) {
        if(!cyclicalTransferRepository.existsById(transferId))
            throw new IllegalStateException(TransferNotExists(transferId));
        cyclicalTransferRepository.deleteById(transferId);
    }

    //Automated method that runs every day at midnight
    @Transactional
    @Scheduled(cron = "0 0 0 * * * ")
    protected void realiseTransfers(){
        Logger logger = LoggerFactory.getLogger(CyclicalTransferService.class);
        List<CyclicalTransfer> cyclicalTransfersList = getTransfers();

        if(!cyclicalTransfersList.isEmpty()){
            for(CyclicalTransfer transfer : cyclicalTransfersList){

                if(!transfer.getReTransferDate().equals(LocalDate.now()))
                    continue;

                Client sender = clientService.getClientOrNull(transfer.getClient().getClientId());
                Client receiver = clientService.getClientByAccountNumber(transfer.getAccountNumber());

                if(sender == null){
                    logger.error("Cannot realise transfer, client doesn't exist anymore, deleting cyclical transfer!!\n");
                    deleteTransfer(transfer.getTransferId());
                    continue;
                }

                if(sender.getBalance() < transfer.getAmount()){
                    logger.warn("Cannot realise transfer, insufficient balance!!\n");
                    emailService.send(
                            sender.getEmail(),
                            emailTemplates.emailTemplateInsufficientBalanceForTransfer(sender,transfer.getTransferId()),
                            "Your cyclical transfer couldn't be realised!"
                    );
                    continue;
                }

                clientService.updateClientBalance(sender,transfer.getAmount(),OUTGOING.name());
                Transfer senderTransfer = new Transfer(
                        transfer.getAmount(),
                        LocalDate.now(),
                        transfer.getCategory(),
                        OUTGOING.name(),
                        transfer.getReceiver(),
                        transfer.getTitle(),
                        transfer.getAccountNumber()
                );
                transferService.addTransfer(senderTransfer);

                if(!(receiver == null)){
                    clientService.updateClientBalance(receiver,transfer.getAmount(),INCOMING.name());
                    Transfer receiverTransfer = new Transfer(
                            transfer.getAmount(),
                            LocalDate.now(),
                            transfer.getCategory(),
                            INCOMING.name(),
                            sender.getFullName(),
                            transfer.getTitle(),
                            transfer.getAccountNumber()
                    );
                    transferService.addTransfer(receiverTransfer);
                }
                transfer.setReTransferDate(transfer.getReTransferDate().plusMonths(1));
                logger.debug("Transfer id:" + transfer.getTransferId() + " finalized!\n");
                cyclicalTransferRepository.save(transfer);
            }
        }
        else logger.debug("No transfer will be realised today!\n");
    }


}
