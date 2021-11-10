package com.OBS.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderType {
    changeClient("Edycja danych klienta"),
    createUser("Utworzenie użytkownika"),
    changeUser("Modyfikacja użytkownika"),
    changeEmployee("Modyfikacja danych pracownika"),
    blockCreditCard("Zablokowanie karty kredytowej"),
    unblockCreditCard("Odblokowanie karty kredytowej"),
    createCreditCard("Wyrób nowej karty kredytowej"),
    destroyCreditCard("Wycofajnie karty kredytowej"),
    loanRequest("Podanie o kredyt");

    private final String type;
}
