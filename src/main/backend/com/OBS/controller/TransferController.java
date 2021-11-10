package com.OBS.controller;

import com.OBS.entity.Transfer;
import com.OBS.requestBodies.FilterTransferFromClient;
import com.OBS.service.TransferService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/transfers")
@AllArgsConstructor
public class TransferController {
    private final TransferService transferService;

    @GetMapping(path = "client/{client_Id}")
    public List<Transfer> getTransfers(@PathVariable Long client_Id, @RequestBody FilterTransferFromClient body){
        return transferService.getTransfers(client_Id,body);
    }

    @PostMapping()
    public void addTransfer(@RequestBody Transfer transfer){ transferService.addTransfer(transfer);}

    @DeleteMapping(path = "{transferId}")
    public void deleteTransfer(@PathVariable long transferId){transferService.deleteTransfer(transferId);}

}
