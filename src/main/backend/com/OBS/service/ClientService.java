package com.OBS.service;

import com.OBS.entity.Client;
import com.OBS.repository.ClientRepository;
import com.OBS.requestBodies.ClientUserBody;
import com.OBS.requestBodies.NamePersonalNum_BirthDateBody;
import com.OBS.requestBodies.UserCredentials;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static com.OBS.enums.TransferType.*;

@Service
@AllArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final AppUserService appUserService;
    private final ClientEmployeeService clientEmployeeService;

    public List<Client> getClients() {
        return clientRepository.findAll();
    }

    public List<Client> getClients(String personalNumber_personName,String date) {
        LocalDate birthDate;
        if(Objects.equals(date, ""))
            birthDate = null;
        else
            birthDate = LocalDate.parse(date);
        if(Objects.equals(personalNumber_personName, "") && birthDate == null)
            return clientRepository.findAll();
        if(Objects.equals(personalNumber_personName, "") && birthDate != null)
            return clientRepository.findAllByDateOfBirth(birthDate);
        if(!Objects.equals(personalNumber_personName, "") && birthDate == null)
            return clientRepository.findAllByFullNameContainsOrPersonalNumberStartsWith(personalNumber_personName,personalNumber_personName);
        return clientRepository.findAllByFullNameContainsOrPersonalNumberStartsWithOrDateOfBirth(personalNumber_personName,personalNumber_personName,birthDate);
    }

    public Client getClient(Long id) {
        return clientRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("Client with given id " + id + "doesn't exists in database")
        );
    }

    public Client getClientOrNull(Long id) {
        if (clientRepository.existsById(id))
            return clientRepository.getById(id);
        else return null;
    }

    public Long getClientIdByUserId(Long appUserId) {
        Client client = clientRepository.getByUser(appUserService.getUser(appUserId));
        if (client == null)
            return null;
        else return client.getClientId();
    }

    public Client getClientByAccountNumber(String accountNumber) {
        if(clientRepository.existsByAccountNumber(accountNumber)){
            return clientRepository.findByAccountNumber(accountNumber);
        }else return null;
    }


    public void addClient(ClientUserBody body) {
        Client client = body.getClient();
        UserCredentials userCredentials = body.getUserCredentials();

        if (clientEmployeeService.existsByPersonalNumber(client.getPersonalNumber())) {
            throw new IllegalStateException("This Personal Number is already taken!");
        }

        if (clientEmployeeService.existsByIdentificationNumber(client.getIdentificationNumber())) {
            throw new IllegalStateException("This Personal Number is already taken!");
        }

        if (clientRepository.existsByPersonalNumber(client.getPersonalNumber())) {
            throw new IllegalStateException("This Personal Number is already taken!");
        }
        if (clientRepository.existsByIdentificationNumber(client.getIdentificationNumber())) {
            throw new IllegalStateException("This Identification Number is already taken!");
        }
        if (clientRepository.existsByAccountNumber(client.getAccountNumber())) {
            throw new IllegalStateException("This Account Number is already taken!");
        }
        client.setUser(appUserService.createAppUser(userCredentials));
        client.setDateOfCreation(LocalDate.now());
        clientRepository.save(client);
    }

    @Transactional
    public void updateClient(ClientUserBody body) {
        Client newClientRecord = body.getClient();
        UserCredentials newUserCredentials = body.getUserCredentials();
        if (clientRepository.existsById(newClientRecord.getClientId())) {
            throw new IllegalStateException("Client with given id:" + newClientRecord.getClientId() + " doesn't exists in database");
        }
        Client currentClientRecord = clientRepository.getById(newClientRecord.getClientId());

        if (clientEmployeeService.existsByPersonalNumber(newClientRecord.getPersonalNumber())) {
            throw new IllegalStateException("This Personal Number is already taken!");
        }

        if (clientEmployeeService.existsByIdentificationNumber(newClientRecord.getIdentificationNumber())) {
            throw new IllegalStateException("This Personal Number is already taken!");
        }

        if (!currentClientRecord.getPersonalNumber().equals(newClientRecord.getPersonalNumber())) {
            if (clientRepository.existsByPersonalNumber(newClientRecord.getPersonalNumber()))
                throw new IllegalStateException("This Personal Number is already taken!");
        }

        if (!currentClientRecord.getIdentificationNumber().equals(newClientRecord.getIdentificationNumber())) {
            if (clientRepository.existsByIdentificationNumber(newClientRecord.getIdentificationNumber()))
                throw new IllegalStateException("This Identification Number is already taken!");
        }

        if (!currentClientRecord.getAccountNumber().equals(newClientRecord.getAccountNumber())) {
            if (clientRepository.existsByAccountNumber(newClientRecord.getAccountNumber()))
                throw new IllegalStateException("This Account Number is already taken!");
        }

        appUserService.updateAppUser(currentClientRecord.getUser().getUserId(), newUserCredentials);

        if (!newUserCredentials.getEmail().isEmpty() && !newUserCredentials.getEmail().equals(currentClientRecord.getEmail()))
            newClientRecord.setEmail(newUserCredentials.getEmail());

        newClientRecord.setUser(currentClientRecord.getUser());

        clientRepository.save(newClientRecord);
    }

    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new IllegalStateException("Can't find client of given id");
        }
        clientRepository.deleteById(id);
    }

    @Transactional
    public void setNumOfCreditCards(Long clientId, String type) {
        Client client = clientRepository.findById(clientId).orElseThrow(
                () -> new IllegalStateException("Client with given id " + clientId + "doesn't exists in database")
        );

        if (client.getNumberOfCreditsCards() == null)
            client.setNumberOfCreditsCards(0);

        if (type.equals("increment"))
            client.setNumberOfCreditsCards(client.getNumberOfCreditsCards() + 1);
        else client.setNumberOfCreditsCards(client.getNumberOfCreditsCards() - 1);
        clientRepository.save(client);
    }

    @Transactional
    public void updateClientBalance(Client client, Float amount, String type) {
        if(type.equals(INCOMING.name()))
            client.setBalance(client.getBalance() + amount);
        else client.setBalance(client.getBalance() - amount);

        clientRepository.save(client);
    }
}

