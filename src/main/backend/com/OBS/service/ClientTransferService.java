package com.OBS.service;

import com.OBS.alternativeBodies.ClientCreditWorthiness;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClientTransferService {
    private final TransferService transferService;
    private final ClientService clientService;

    public ClientCreditWorthiness getClientWorthiness(Long clientId,int months){
        return transferService.getClientWorthiness(clientId,months);
    }
}
