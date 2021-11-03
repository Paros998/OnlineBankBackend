package com.OBS.service;

import com.OBS.entity.Client;
import com.OBS.entity.CreditCard;
import com.OBS.repository.ClientRepository;
import com.OBS.repository.CreditCardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.function.Supplier;

@Service
@AllArgsConstructor
public class CreditCardService {
    private final CreditCardRepository creditCardRepository;
    private final ClientRepository clientRepository;

    private final Supplier<IllegalStateException> handleError = () -> {
        String errorMessage = "Can't find credit card of given id";
        return new IllegalStateException(errorMessage);
    };

    public List<CreditCard> getCreditCards() {
        return creditCardRepository.findAll();
    }

    public CreditCard getCreditCard(Long cardId) {
        return creditCardRepository.findById(cardId).orElseThrow(handleError);
    }

    public List<CreditCard> getClientsCreditCards(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new IllegalStateException("Can't find client of given id");
        }
        return creditCardRepository.findAllByClient_clientId(clientId);
    }

    @Transactional
    public void addCreditCard(String accountNumber, CreditCard creditCard) {
        if (!clientRepository.existsByAccountNumber(accountNumber)) {
            throw new IllegalStateException("Can't find client of given account number");
        }

        Client client = clientRepository.findByAccountNumber(accountNumber);
        // TODO move logic to clientService
        if (client.getNumberOfCreditsCards() == null) {
            client.setNumberOfCreditsCards(0);
        }

        client.setNumberOfCreditsCards(client.getNumberOfCreditsCards() + 1);
        creditCard.setClient(client);

        clientRepository.save(client);
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
}