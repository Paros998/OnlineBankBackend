package com.OBS.repository;

import com.OBS.entity.Visit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long>, JpaSpecificationExecutor<Visit> {
    List<Visit> findAllByEmployee_EmployeeId(Long id);
    List<Visit> findAllByEmployeeNull(Sort s);
}
