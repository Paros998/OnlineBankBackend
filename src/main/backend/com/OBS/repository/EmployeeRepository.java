package com.OBS.repository;

import com.OBS.auth.entity.AppUser;
import com.OBS.entity.Client;
import com.OBS.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findEmployeeByPersonalNumber(String personalNumber);

    @Query("select e from Employee e where e.dateOfBirth = :date or (e.fullName like '%:string%' or e.personalNumber like ':string%') ")
    List<Employee> findAllByStringOrDate(@Param("string")String personalNumber_personName, @Param("date") LocalDate birthDate);

    Optional<Employee> findByFullName(String fullName);

    boolean existsByPersonalNumber(String personalNumber);

    boolean existsByIdentificationNumber(String identificationNumber);

    Employee getByUser(AppUser user);
}
