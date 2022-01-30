package com.OBS.service;

import com.OBS.entity.Client;
import com.OBS.entity.CreditCard;
import com.OBS.repository.CreditCardRepository;
import com.OBS.searchers.specificators.Specifications;
import com.OBS.service.interfaces.systemFacade.CreditCardServiceFacade;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class CreditCardService implements CreditCardServiceFacade {
    private final CreditCardRepository creditCardRepository;
    private final ClientService clientService;
    private final String errorMessage = "Can't find credit card of given id";

    public List<CreditCard> getCreditCards() {
        return creditCardRepository.findAll();
    }

    public CreditCard getCreditCard(Long cardId) {
        return creditCardRepository.findById(cardId).orElseThrow(() -> new IllegalStateException(errorMessage));
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
        CreditCard oldCreditCard = creditCardRepository.findById(cardId).orElseThrow(
                () -> new IllegalStateException(errorMessage)
        );
        oldCreditCard.setIsActive(!oldCreditCard.getIsActive());
        creditCardRepository.save(oldCreditCard);
    }

    @Transactional
    public void deleteCreditCard(Long cardId) {
        if (!creditCardRepository.existsById(cardId)) {
            throw new IllegalStateException(errorMessage);
        }
        CreditCard creditCard = creditCardRepository.getById(cardId);
        Client client = clientService.getClient(creditCard.getClient().getClientId());
        clientService.setNumOfCreditCards(client.getClientId(),"decrement");

        creditCardRepository.deleteById(cardId);
    }

    public boolean existsByCardNumber(String cardNumber) {
        return creditCardRepository.existsByCardNumber(cardNumber);
    }

    public List<CreditCard> getCreditCardsBySpecification(Specifications<CreditCard> creditCardSpecification) {
        return creditCardRepository.findAll(creditCardSpecification);
    }
}
