package com.OBS.service;

import com.OBS.entity.Loan;
import com.OBS.entity.LoanRate;
import com.OBS.enums.SearchOperation;
import com.OBS.repository.LoanRateRepository;
import com.OBS.searchers.SearchCriteria;
import com.OBS.searchers.specificators.Specifications;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class LoanRateService {
    private final LoanRateRepository loanRateRepository;

    public List<LoanRate> getRates() {
        return loanRateRepository.findAll();
    }

    public List<LoanRate> getRates(Long loanId) {
        return loanRateRepository.findAllByLoan_loanId(loanId);
    }

    public void addRate( Loan loan){
        LoanRate loanRate = new LoanRate(loan);
        loanRateRepository.save(loanRate);
    }

    public int getNumOfRatesPayed(Long loanId) {
        return loanRateRepository.countByLoan_loanId(loanId);
    }

    public boolean checkIfPayed(LocalDate nextRatePayDay) {
        //TODO check if this works
        return loanRateRepository.existsByPayDateBetween(nextRatePayDay.minusMonths(1),nextRatePayDay);
    }

    public void deleteRate(Long loanRateId){loanRateRepository.deleteById(loanRateId);}

}
