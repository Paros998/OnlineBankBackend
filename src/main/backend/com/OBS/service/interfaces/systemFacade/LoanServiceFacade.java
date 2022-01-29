package com.OBS.service.interfaces.systemFacade;

import com.OBS.entity.Loan;
import com.sun.istack.NotNull;
import org.springframework.stereotype.Service;

@Service
public interface LoanServiceFacade {
    void addLoan(@NotNull Loan newLoan);
}
