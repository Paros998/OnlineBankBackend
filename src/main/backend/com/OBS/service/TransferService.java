package com.OBS.service;

import com.OBS.entity.Transfer;
import com.OBS.repository.TransferRepository;
import com.OBS.requestBodies.FilterTransferFromClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TransferService {
    private final TransferRepository transferRepository;

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

    public void deleteTransfer(long transferId) {
        if(!transferRepository.existsById(transferId))
            throw new IllegalStateException("Transfer with given id " + transferId + " doesn't exists");
        transferRepository.deleteById(transferId);
    }
}
