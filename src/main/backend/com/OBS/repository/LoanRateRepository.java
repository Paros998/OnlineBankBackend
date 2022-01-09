package com.OBS.repository;

import com.OBS.entity.LoanRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRateRepository extends JpaRepository<LoanRate,Long>, JpaSpecificationExecutor<LoanRate> {
    List<LoanRate> findAllByLoan_loanId(Long loanId);

    Boolean existsByPayDateBetween(LocalDate dateBefore,LocalDate dateAfter);

    int countByLoan_loanId(Long loanId);
}
