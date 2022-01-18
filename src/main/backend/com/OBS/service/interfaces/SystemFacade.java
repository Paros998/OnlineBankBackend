package com.OBS.service.interfaces;

import com.OBS.alternativeBodies.CreateCreditCardModel;
import com.OBS.alternativeBodies.UserCredentials;
import com.OBS.entity.Client;
import com.OBS.entity.CreditCard;
import com.OBS.entity.Employee;
import com.OBS.entity.Loan;
import org.springframework.stereotype.Service;

@Service
public interface SystemFacade {
    void createNewUser(UserCredentials userCredentials);
    void updateAppUser(UserCredentials userCredentials);
    void updateClient(Client client);
    void updateEmployee(Employee employee);
    void blockCreditCard(CreditCard creditCard);
    void discardCreditCard(CreditCard creditCard);
    void createCreditCard(CreateCreditCardModel creditCardModel);
    void unblockCreditCard(CreditCard creditCard);
    void createLoan(Loan loan, Client client);
}
