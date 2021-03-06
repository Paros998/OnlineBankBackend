package com.OBS.controller;

import com.OBS.alternativeBodies.KeyValueObject;
import com.OBS.alternativeBodies.ValueAndPercent;
import com.OBS.entity.Transfer;
import com.OBS.service.TransferService;
import lombok.AllArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Between;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.GreaterThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.LessThanOrEqual;
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

    @GetMapping(path = "/client/{client_Id}")
    public List<Transfer> getTransfers(
            @And({
                    @Spec(path = "transferDate", params = "dateFrom", config = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", spec = GreaterThanOrEqual.class),
                    @Spec(path = "transferDate", params = "dateTo", config = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", spec = LessThanOrEqual.class),
                    @Spec(path = "client.clientId", pathVars = "client_Id", spec = Equal.class),
                    @Spec(path = "type", params = "transferType", spec = Equal.class),
                    @Spec(path = "category", params = "transferCategory", spec = Equal.class)
            }) Specification<Transfer> filterTransferSpec) {
        return transferService.getTransfers(filterTransferSpec);
    }

    @GetMapping(path = "/client/{client_Id}/1-month-history")
    public List<KeyValueObject<String, ValueAndPercent>> getClientHistoryOf1Month(@PathVariable Long client_Id) {
        return transferService.getClientHistory(client_Id, 1);
    }


    @PostMapping()
    public void addTransfer(@RequestBody Transfer transfer) {
        transferService.performTransfer(transfer);
    }

    @DeleteMapping(path = "{transferId}")
    public void deleteTransfer(@PathVariable Long transferId) {
        transferService.deleteTransfer(transferId);
    }
}
