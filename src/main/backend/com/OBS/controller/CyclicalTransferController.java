package com.OBS.controller;

import com.OBS.entity.CyclicalTransfer;
import com.OBS.service.CyclicalTransferService;
import lombok.AllArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Between;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;
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

    @GetMapping(path = "/coming/client/{clientId}")
    public List<CyclicalTransfer> getComingTransfers(@PathVariable Long clientId) {
        return cyclicalTransferService.getComingTransfers(clientId);
    }

    @RequestMapping(path = "/client/{clientId}")
    public List<CyclicalTransfer> getClientTransfers(
            @And({
                    @Spec(path = "client.clientId", pathVars = "clientId", spec = Equal.class),
                    @Spec(path = "category", params = "transferCategory", spec = Equal.class),
                    @Spec(path = "reTransferDate", params = {"dateFrom", "dateTo"}, config = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", spec = Between.class)
            }) Specification<CyclicalTransfer> filterCyclicalTransferSpec
    ) {
        return cyclicalTransferService.getClientTransfers(filterCyclicalTransferSpec);
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
