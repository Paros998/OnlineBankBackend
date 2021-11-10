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

    @GetMapping(path = "client/{clientId}")
    public List<CreditCard> getClientsCreditCards(@PathVariable("clientId") Long clientId) {
        return creditCardService.getClientsCreditCards(clientId);
    }

    @PostMapping(path = "{clientId}")
    public void addCreditCard(@PathVariable("clientId") Long clientId, @RequestBody CreditCard creditCard) {
        creditCardService.addCreditCard(clientId, creditCard);
    }

    @PutMapping(path = "{id}/active")
    public void switchActiveStateOfCreditCard(@PathVariable("id") Long cardId) {
        creditCardService.switchActiveStateOfCreditCard(cardId);
    }

    @DeleteMapping(path = "{id}")
    public void deleteCreditCard(@PathVariable("id") Long cardId) {
        creditCardService.deleteCreditCard(cardId);
    }
}
