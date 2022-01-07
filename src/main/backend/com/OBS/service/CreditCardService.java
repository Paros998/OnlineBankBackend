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
    private final ClientService clientService;

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
        Client client = clientService.getClient(clientId);
        return creditCardRepository.findAllByClient_clientId(client.getClientId());
    }

    @Transactional
    public void addCreditCard(Long clientId, CreditCard creditCard) {

        clientService.setNumOfCreditCards(clientId,"increment");

        Client client = clientService.getClient(clientId);

        creditCard.setClient(client);

        creditCardRepository.save(creditCard);
    }

    @Transactional
    public void switchActiveStateOfCreditCard(Long cardId) {
        CreditCard oldCreditCard = creditCardRepository.findById(cardId).orElseThrow(handleError);
        oldCreditCard.setIsActive(!oldCreditCard.getIsActive());
        creditCardRepository.save(oldCreditCard);
    }

    public void deleteCreditCard(Long cardId) {
        if (!creditCardRepository.existsById(cardId)) {
            throw handleError.get();
        }
        CreditCard creditCard = creditCardRepository.getById(cardId);
        Client client = clientService.getClient(creditCard.getClient().getClientId());
        clientService.setNumOfCreditCards(client.getClientId(),"decrement");

        creditCardRepository.deleteById(cardId);
    }

    public boolean existsByCardNumber(String cardNumber) {
        return creditCardRepository.existsByCardNumber(cardNumber);
    }
}
