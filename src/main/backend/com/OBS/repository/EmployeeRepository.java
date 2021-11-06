package com.OBS.repository;

import com.OBS.auth.entity.AppUser;
import com.OBS.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findEmployeeByPersonalNumber(String personalNumber);

    List<Employee> findAllByFullNameAndPersonalNumber(String personalNumber, String fullName);

    Optional<Employee> findByFullName(String fullName);

    boolean existsByPersonalNumber(String personalNumber);

    boolean existsByIdentificationNumber(String identificationNumber);

    Employee getByUser(AppUser user);
}
