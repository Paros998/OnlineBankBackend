package com.OBS.controller;

import com.OBS.entity.CyclicalTransfer;
import com.OBS.service.CyclicalTransferService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/cyclical-transfers")
public class CyclicalTransferController {
    private final CyclicalTransferService cyclicalTransferService;

    @GetMapping(path = "/{transferId}")
    public CyclicalTransfer getTransfer(@PathVariable Long transferId){
        return cyclicalTransferService.getTransfer(transferId);
    }

    @GetMapping(path = "/client/{clientId}")
    public List<CyclicalTransfer> getClientTransfers(@PathVariable Long clientId){
        return cyclicalTransferService.getClientTransfers(clientId);
    }

    @PostMapping()
    public void addTransfer(@RequestBody CyclicalTransfer cyclicalTransfer){
        cyclicalTransferService.addTransfer(cyclicalTransfer);
    }

    @PutMapping(path = "{transferId}")
    public void updateTransfer(@RequestBody CyclicalTransfer cyclicalTransfer,@PathVariable Long transferId){
        cyclicalTransferService.updateTransfer(cyclicalTransfer,transferId);
    }

    @DeleteMapping(path = "{transferId}")
    public void deleteTransfer(@PathVariable Long transferId){ cyclicalTransferService.deleteTransfer(transferId); }
}
