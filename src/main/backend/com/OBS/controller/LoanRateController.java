package com.OBS.controller;

import com.OBS.entity.LoanRate;
import com.OBS.service.LoanRateService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/loans-rates")
@AllArgsConstructor
public class LoanRateController {
    private final LoanRateService loanRateService;

    @GetMapping(path = "/loan/{loanId}")
    public List<LoanRate> getLoanRates(@PathVariable Long loanId){ return loanRateService.getRates(loanId);}
}
