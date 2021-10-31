package com.OBS.service;

import com.OBS.entity.Client;
import com.OBS.repository.ClientRepository;
import com.OBS.requestBodies.ClientUserBody;
import com.OBS.requestBodies.UserCredentials;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final AppUserService appUserService;

    public List<Client> getClients() {
        return clientRepository.findAll();
    }

    public List<Client> getClients(String fullName, String personalNumber) {
        return clientRepository.findAllByFullNameAndPersonalNumber(fullName,personalNumber);
    }

    public Client getClient(Long id) {
        return clientRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("Client with given id " + id + "doesn't exists in database")
        );
    }

    //TODO check for employees with same email personalNumber and Identification Number
    public void addClient(ClientUserBody body) {
        Client client = body.getClient();
        UserCredentials userCredentials = body.getUserCredentials();

        if (clientRepository.existsByPersonalNumber(client.getPersonalNumber())) {
            throw new IllegalStateException("This Personal Number is already taken!");
        }
        if (clientRepository.existsByIdentificationNumber(client.getIdentificationNumber())) {
            throw new IllegalStateException("This Identification Number is already taken!");
        }
        if(clientRepository.existsByAccountNumber(client.getAccountNumber())){
            throw new IllegalStateException("This Account Number is already taken!");
        }
        client.setUser(appUserService.createAppUser(userCredentials));
        clientRepository.save(client);
    }
    //TODO check for employees with same email personalNumber and Identification Number
    @Transactional
    public void updateClient(ClientUserBody body) {
        Client newClientRecord = body.getClient();
        UserCredentials newUserCredentials = body.getUserCredentials();
        if(clientRepository.existsById(newClientRecord.getClientId())){
            throw new IllegalStateException("Client with given id:" + newClientRecord.getClientId() + " doesn't exists in database");
        }
        Client currentClientRecord = clientRepository.getById(newClientRecord.getClientId());

        if(!currentClientRecord.getPersonalNumber().equals(newClientRecord.getPersonalNumber())){
            if(clientRepository.existsByPersonalNumber(newClientRecord.getPersonalNumber()))
                throw new IllegalStateException("This Personal Number is already taken!");
        }

        if(!currentClientRecord.getIdentificationNumber().equals(newClientRecord.getIdentificationNumber())){
            if(clientRepository.existsByIdentificationNumber(newClientRecord.getIdentificationNumber()))
                throw new IllegalStateException("This Identification Number is already taken!");
        }

        if(!currentClientRecord.getAccountNumber().equals(newClientRecord.getAccountNumber())){
            if(clientRepository.existsByAccountNumber(newClientRecord.getAccountNumber()))
                throw new IllegalStateException("This Account Number is already taken!");
        }

        appUserService.updateAppUser(currentClientRecord.getUser().getUserId(),newUserCredentials);

        if(!newUserCredentials.getEmail().isEmpty() && !newUserCredentials.getEmail().equals(currentClientRecord.getEmail()))
            newClientRecord.setEmail(newUserCredentials.getEmail());

        newClientRecord.setUser(currentClientRecord.getUser());

        clientRepository.save(newClientRecord);
    }

    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new IllegalStateException("Can't find announcement of given id");
        }
        clientRepository.deleteById(id);
    }
}
