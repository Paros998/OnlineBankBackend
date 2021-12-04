package com.OBS.repository;

import com.OBS.auth.entity.AppUser;
import com.OBS.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByPersonalNumber(String personalNumber);

    boolean existsByIdentificationNumber(String IdentificationNumber);

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByEmail(String email);

    Client findByAccountNumber(String accountNumber);

    Client getByUser(AppUser user);

    @Query("select c from Client c where c.dateOfBirth = :date or (c.fullName like '%:string%' or c.personalNumber like ':string%') ")
    List<Client> findAllByStringOrDate(@Param("string")String personalNumber_personName,@Param("date") LocalDate birthDate);
}
