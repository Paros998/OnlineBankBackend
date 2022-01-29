package com.OBS.service;

import com.OBS.alternativeBodies.CreateCreditCardModel;
import com.OBS.alternativeBodies.UserCredentials;
import com.OBS.auth.AppUserRole;
import com.OBS.auth.entity.AppUser;
import com.OBS.entity.Client;
import com.OBS.entity.CreditCard;
import com.OBS.entity.Employee;
import com.OBS.entity.Loan;
import com.OBS.service.interfaces.SystemFacade;
import com.OBS.service.interfaces.systemFacade.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;

@Service
@AllArgsConstructor
public class SystemService implements SystemFacade {
    private final AppUserServiceFacade appUserService;
    private final ClientServiceFacade clientService;
    private final EmployeeServiceFacade employeeService;
    private final CreditCardServiceFacade creditCardService;
    private final LoanServiceFacade loanService;

    public void createNewUser(UserCredentials userCredentials){
        AppUser user = appUserService.createAppUser(userCredentials);
        if(userCredentials.getAppUserRole() == AppUserRole.CLIENT){
            Client oldClient = clientService.getClientByEmail(userCredentials.getEmail());
            oldClient.setUser(user);
            clientService.updateClient(oldClient);
            user.setClient(oldClient);
        }else {
            Employee oldEmployee = employeeService.getEmployeeByEmail(userCredentials.getEmail());
            oldEmployee.setUser(user);
            employeeService.updateEmployee(oldEmployee);
            user.setEmployee(oldEmployee);
        }
        appUserService.updateAppUser(user);
    }

    public void updateAppUser(UserCredentials userCredentials){
        AppUser appUser = appUserService.getUserByEmail(userCredentials.getEmail());
        appUserService.updateAppUser(appUser.getUserId(),userCredentials);
    }

    public void updateClient(Client client){
        clientService.updateClient(client);
    }

    public void updateEmployee(Employee employee){
        employeeService.updateEmployee(employee);
    }

    public void blockCreditCard(CreditCard creditCard) {
        creditCardService.switchActiveStateOfCreditCard(creditCard.getCardId());
    }

    public void discardCreditCard(CreditCard creditCard) {
        creditCardService.deleteCreditCard(creditCard.getCardId());
    }

    public void createCreditCard(CreateCreditCardModel creditCardModel) {
        Random random = new Random();
        CreditCard creditCard = new CreditCard();
        creditCard.setPinNumber(creditCardModel.getPinNumber());
        creditCard.setIsActive(true);
        creditCard.setExpireDate(LocalDate.now().plusYears(3));
        creditCard.setCvvNumber((random.nextInt(99999) + 9869 ) % 1000);

        String cardNumber = "1099 20";
        do {
            for (int i = 0; i < 11; i++) {
                if(i == (2 | 7))
                    cardNumber += " ";
                else cardNumber += random.nextInt(10);
            }
            cardNumber += '8';
        } while(creditCardService.existsByCardNumber(cardNumber));
                
        creditCard.setCardNumber(cardNumber);
        
        creditCardService.addCreditCard(creditCard.getClient().getClientId(),creditCard);
    }

    public void unblockCreditCard(CreditCard creditCard) {
        creditCardService.switchActiveStateOfCreditCard(creditCard.getCardId());
    }

    public void createLoan(Loan loan, Client client) {
        loan.setClient(client);
        loanService.addLoan(loan);
    }
}
