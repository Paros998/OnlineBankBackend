package com.OBS.controller;

import com.OBS.entity.CreditCard;
import com.OBS.service.CreditCardService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/credit-cards")
@AllArgsConstructor
public class CreditCardController {
    private final CreditCardService creditCardService;

    @GetMapping(path = "{id}")
    public CreditCard getCreditCard(@PathVariable("id") Long cardId) {
        return creditCardService.getCreditCard(cardId);
    }

    @GetMapping(path = "{clientId}")
    public List<CreditCard> getClientsCreditCards(@PathVariable("clientId") Long clientId) {
        return creditCardService.getClientsCreditCards(clientId);
    }

    @PostMapping(path = "{accountNumber}")
    public void addCreditCard(@PathVariable("accountNumber") String accountNumber, @RequestBody CreditCard creditCard) {
        creditCardService.addCreditCard(accountNumber, creditCard);
    }

    @PutMapping(path = "{id}")
    public void switchActiveStateOfCreditCard(@PathVariable("id") Long cardId, @RequestBody CreditCard creditCard) {
        creditCardService.switchActiveStateOfCreditCard(cardId, creditCard);
    }

    @DeleteMapping(path = "{id}")
    public void deleteCreditCard(@PathVariable("id") Long cardId) {
        creditCardService.deleteCreditCard(cardId);
    }
}
