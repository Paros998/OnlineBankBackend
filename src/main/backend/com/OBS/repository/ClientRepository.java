package com.OBS.repository;

import com.OBS.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client,Long> {
    List<Client> findAllByFullNameAndPersonalNumber(String fullName, String personalNumber);

    boolean existsByPersonalNumber(String personalNumber);
    boolean existsByIdentificationNumber(String IdentificationNumber);

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByEmail(String email);
}
