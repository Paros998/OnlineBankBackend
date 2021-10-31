package com.OBS.service;

import com.OBS.entity.CreditCard;
import com.OBS.repository.CreditCardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
@AllArgsConstructor
public class CreditCardService {
    private final CreditCardRepository creditCardRepository;

    private Supplier<IllegalStateException> handleError;

    public List<CreditCard> getCreditCards() {
        return creditCardRepository.findAll();
    }

    // TODO check if client exists
    // TODO check if accountNumber exists
    public void addCreditCard(String accountNumber, CreditCard creditCard) {
        creditCardRepository.save(creditCard);
    }

    public void switchActiveStateOfCreditCard(Long cardId, CreditCard creditCard) {
        CreditCard oldCreditCard = creditCardRepository.findById(cardId).orElseThrow(handleError);
        oldCreditCard.setIsActive(creditCard.getIsActive());
    }

    public void deleteCreditCard(Long cardId) {
        if (!creditCardRepository.existsById(cardId)) {
            throw handleError.get();
        }
        creditCardRepository.deleteById(cardId);
    }

    public CreditCard getCreditCard(Long cardId) {
        return creditCardRepository.findById(cardId).orElseThrow(handleError);
    }
}
