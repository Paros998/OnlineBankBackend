package com.OBS.controller;

import com.OBS.entity.Transfer;
import com.OBS.service.TransferService;
import lombok.AllArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Between;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/transfers")
@AllArgsConstructor
public class TransferController {
    private final TransferService transferService;

    @GetMapping(path = "recent/client/{client_Id}")
    public List<Transfer> getRecentTransfers(@PathVariable Long client_Id) {
        return transferService.getRecentTransfers(client_Id);
    }

    @RequestMapping(path = "/client/{client_Id}")
    public List<Transfer> getTransfers(
            @And({
                    @Spec(path = "client.clientId", pathVars = "client_Id", spec = Equal.class),
                    @Spec(path = "type", params = "transferType", spec = Equal.class),
                    @Spec(path = "category", params = "transferCategory", spec = Equal.class),
                    @Spec(path = "transferDate", params = {"dateFrom", "dateTo"}, config = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", spec = Between.class)
            }) Specification<Transfer> filterTransferSpec
    ) {
        return transferService.getTransfers(filterTransferSpec);
    }

    //TODO add endpoint for 30days history of payments within categories

    @PostMapping()
    public void addTransfer(@RequestBody Transfer transfer) { transferService.performTransfer(transfer); }

    @DeleteMapping(path = "{transferId}")
    public void deleteTransfer(@PathVariable Long transferId) { transferService.deleteTransfer(transferId); }
}
