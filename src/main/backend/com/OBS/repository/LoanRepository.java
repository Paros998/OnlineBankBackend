package com.OBS.repository;

import com.OBS.entity.Loan;
import com.OBS.searchers.specificators.Specifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan,Long>, JpaSpecificationExecutor<Loan> {
    Optional<Loan> findByClient_clientIdAndIsActive(Long clientId, boolean isActive);
    List<Loan> findAllByClient_clientId(Long clientId);
}
