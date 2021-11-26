package com.OBS.controller;

import com.OBS.entity.Loan;
import com.OBS.requestBodies.LoanBody;
import com.OBS.service.LoanService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/loans")
public class LoanController {
    private final LoanService loanService;

    @GetMapping(path = "/calculate")
    public Loan getFutureLoan(@RequestBody LoanBody body){return loanService.calculateLoan(body);}

    @GetMapping(path = "/{loanId}")
    public Loan getLoan(@PathVariable Long loanId){return loanService.getLoan(loanId);}

    @PostMapping()
    public void addLoan(@RequestBody LoanBody body){loanService.addLoan(body);}

    @PatchMapping(path = "/{loanId}")
    public void setInactive(@PathVariable Long loanId){loanService.setInactive(loanId);}

    @DeleteMapping(path = "/{loanId}")
    public void deleteLoan(@PathVariable Long loanId){loanService.deleteLoan(loanId);}

    @PatchMapping(path = "/pay-rate/{clientId}")
    public void payLoanRate(@PathVariable Long clientId){loanService.realizePayment(clientId);}

    //TODO add endpoint for client to get future loan calculated

}
