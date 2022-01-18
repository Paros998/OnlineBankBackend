package com.OBS.enums;

import com.OBS.alternativeBodies.CreateCreditCardModel;
import com.OBS.alternativeBodies.UserCredentials;
import com.OBS.entity.*;
import com.OBS.service.interfaces.SystemFacade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import javax.json.bind.Jsonb;

@Service
@Getter
@AllArgsConstructor
public enum OrderType {

    changeClient("Edycja danych klienta"){
        @Override
        public void finishOrder(SystemFacade systemService, Jsonb jsonb, Order order) {
            systemService.createNewUser(jsonb.fromJson(order.getRequestBody(), UserCredentials.class));
        }

        @Override
        public String toString(){
            return "Edycja danych klienta";
        }
    },

    createUser("Utworzenie użytkownika"){
        @Override
        public void finishOrder(SystemFacade systemService, Jsonb jsonb, Order order) {
            systemService.updateClient(jsonb.fromJson(order.getRequestBody(), Client.class));
        }

        @Override
        public String toString(){
            return "Utworzenie użytkownika";
        }
    },

    changeUser("Modyfikacja użytkownika"){
        @Override
        public void finishOrder(SystemFacade systemService, Jsonb jsonb, Order order) {
            systemService.updateAppUser(jsonb.fromJson(order.getRequestBody(),UserCredentials.class));
        }

        @Override
        public String toString(){
            return "Modyfikacja użytkownika";
        }
    },

    changeEmployee("Modyfikacja danych pracownika"){
        @Override
        public void finishOrder(SystemFacade systemService, Jsonb jsonb, Order order) {
            systemService.updateEmployee(jsonb.fromJson(order.getRequestBody(), Employee.class));
        }

        @Override
        public String toString(){
            return "Modyfikacja danych pracownika";
        }
    },

    blockCreditCard("Zablokowanie karty kredytowej"){
        @Override
        public void finishOrder(SystemFacade systemService, Jsonb jsonb, Order order) {
            systemService.blockCreditCard(jsonb.fromJson(order.getRequestBody(), CreditCard.class));
        }

        @Override
        public String toString(){
            return "Zablokowanie karty kredytowej";
        }
    },

    unblockCreditCard("Odblokowanie karty kredytowej"){
        @Override
        public void finishOrder(SystemFacade systemService, Jsonb jsonb, Order order) {
            systemService.discardCreditCard(jsonb.fromJson(order.getRequestBody(),CreditCard.class));
        }

        @Override
        public String toString(){
            return "Odblokowanie karty kredytowej";
        }
    },

    createCreditCard("Wyrób nowej karty kredytowej"){
        @Override
        public void finishOrder(SystemFacade systemService, Jsonb jsonb, Order order) {
            systemService.createCreditCard(jsonb.fromJson(order.getRequestBody(), CreateCreditCardModel.class));
        }

        @Override
        public String toString(){
            return "Wyrób nowej karty kredytowej";
        }
    },

    destroyCreditCard("Wycofanie karty kredytowej"){
        @Override
        public void finishOrder(SystemFacade systemService, Jsonb jsonb, Order order) {
            systemService.unblockCreditCard(jsonb.fromJson(order.getRequestBody(),CreditCard.class));
        }

        @Override
        public String toString(){
            return "Wycofanie karty kredytowej";
        }
    },

    loanRequest("Podanie o kredyt"){
        @Override
        public void finishOrder(SystemFacade systemService, Jsonb jsonb, Order order) {
            systemService.createLoan(jsonb.fromJson(order.getRequestBody(), Loan.class),order.getClient());
        }

        @Override
        public String toString(){
            return "Podanie o kredyt";
        }
    };

    private final String type;

    abstract public void finishOrder(SystemFacade systemService, Jsonb jsonb, Order order);
}
