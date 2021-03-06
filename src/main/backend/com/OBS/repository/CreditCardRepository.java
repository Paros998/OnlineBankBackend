package com.OBS.repository;

import com.OBS.entity.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long>, JpaSpecificationExecutor<CreditCard> {
    List<CreditCard> findAllByClient_clientId(Long clientId);

    boolean existsByCardNumber(String cardNumber);
}
