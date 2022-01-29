package com.OBS.service.interfaces.systemFacade;

import com.OBS.entity.CreditCard;
import com.sun.istack.NotNull;
import org.springframework.stereotype.Service;

@Service
public interface CreditCardServiceFacade {
    void switchActiveStateOfCreditCard(Long cardId);
    void deleteCreditCard(Long cardId);
    boolean existsByCardNumber(String cardNumber);
    void addCreditCard(Long clientId, @NotNull CreditCard creditCard);
}
