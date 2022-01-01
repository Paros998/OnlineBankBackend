package com.OBS.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderType {
    changeClient("Edycja danych klienta"){
        @Override
        public String toString(){
            return "Edycja danych klienta";
        }
    },
    createUser("Utworzenie użytkownika"){
        @Override
        public String toString(){
            return "Utworzenie użytkownika";
        }
    },
    changeUser("Modyfikacja użytkownika"){
        @Override
        public String toString(){
            return "Modyfikacja użytkownika";
        }
    },
    changeEmployee("Modyfikacja danych pracownika"){
        @Override
        public String toString(){
            return "Modyfikacja danych pracownika";
        }
    },
    blockCreditCard("Zablokowanie karty kredytowej"){
        @Override
        public String toString(){
            return "Zablokowanie karty kredytowej";
        }
    },
    unblockCreditCard("Odblokowanie karty kredytowej"){
        @Override
        public String toString(){
            return "Odblokowanie karty kredytowej";
        }
    },
    createCreditCard("Wyrób nowej karty kredytowej"){
        @Override
        public String toString(){
            return "Wyrób nowej karty kredytowej";
        }
    },
    destroyCreditCard("Wycofanie karty kredytowej"){
        @Override
        public String toString(){
            return "Wycofanie karty kredytowej";
        }
    },
    loanRequest("Podanie o kredyt"){
        @Override
        public String toString(){
            return "Podanie o kredyt";
        }
    };

    private final String type;
}
