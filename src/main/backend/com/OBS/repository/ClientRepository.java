package com.OBS.repository;

import com.OBS.auth.entity.AppUser;
import com.OBS.entity.Client;
import com.OBS.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client> {

    boolean existsByPersonalNumber(String personalNumber);

    boolean existsByIdentificationNumber(String IdentificationNumber);

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByEmail(String email);

    Client findByAccountNumber(String accountNumber);

    Client getByUser(AppUser user);

    List<Client> findAllByDateOfCreationBetweenOrderByDateOfCreationDesc(LocalDateTime dateOfCreation, LocalDateTime dateOfCreation2);
}
