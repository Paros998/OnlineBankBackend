package com.OBS.service;

import com.OBS.auth.entity.AppUser;
import com.OBS.entity.Client;
import com.OBS.entity.Employee;
import com.OBS.enums.SearchOperation;
import com.OBS.repository.ClientRepository;
import com.OBS.alternativeBodies.ClientUserBody;
import com.OBS.alternativeBodies.UserCredentials;
import com.OBS.searchers.SearchCriteria;
import com.OBS.searchers.specificators.Specifications;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
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
        return clientRepository.findAll(Sort.by(Sort.Direction.DESC,"dateOfBirth"));
    }

    public List<Client> getClients(Specification<Client> clientSpecificationFilter) {
        return clientRepository.findAll(clientSpecificationFilter,Sort.by(Sort.Direction.DESC,"dateOfBirth"));
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

    public List<Client> getLatestClients(Integer days) {
        LocalDateTime today = LocalDateTime.now();
        Specifications<Client> findAllByCreationDateBefore = new Specifications<Client>()
                .add(new SearchCriteria("dateOfCreation",today.minusDays(days), SearchOperation.GREATER_THAN_EQUAL_DATE));
        return clientRepository.findAll(findAllByCreationDateBefore,Sort.by(Sort.Direction.DESC, "dateOfCreation"));
    }

    public void assignUserToClient(Client client,AppUser user){
        clientEmployeeService.assignUser(client,user);
    }

    public void addClient(ClientUserBody body) {
        Client client = body.getClient();
        UserCredentials userCredentials = body.getUserCredentials();

        clientEmployeeService.checkForWithPN_IN(client);

        clientEmployeeService.checkForClientErrors(client);

        client.setUser(appUserService.createAppUser(userCredentials));
        client.setDateOfCreation(LocalDateTime.now());
        clientRepository.save(client);
    }

    public void addOnlyClient(Client client){
        clientEmployeeService.checkForWithPN_IN(client);
        clientEmployeeService.checkForClientErrors(client);

        client.setDateOfCreation(LocalDateTime.now());
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

        clientEmployeeService.checkForWithPN_IN(newClientRecord);

        clientEmployeeService.checkForErrorsBetweenOldEntities(currentClientRecord,newClientRecord);

        appUserService.updateAppUser(currentClientRecord.getUser().getUserId(), newUserCredentials);

        if (!newUserCredentials.getEmail().isEmpty() && !newUserCredentials.getEmail().equals(currentClientRecord.getEmail()))
            newClientRecord.setEmail(newUserCredentials.getEmail());

        newClientRecord.setUser(currentClientRecord.getUser());

        clientRepository.save(newClientRecord);
    }

    @Transactional
    public void updateClient(Client newClientRecord){
        if (!clientRepository.existsById(newClientRecord.getClientId())) {
            throw new IllegalStateException("Client with given id:" + newClientRecord.getClientId() + " doesn't exists in database");
        }
        Client currentClientRecord = clientRepository.getById(newClientRecord.getClientId());

        clientEmployeeService.checkForWithPN_IN(newClientRecord);

        clientEmployeeService.checkForErrorsBetweenOldEntities(currentClientRecord,newClientRecord);

        AppUser clientUser = currentClientRecord.getUser();

        if(!Objects.equals(clientUser.getEmail(), newClientRecord.getEmail())){
            clientUser.setEmail(newClientRecord.getEmail());
        }

        appUserService.updateAppUser(clientUser);

        newClientRecord.setUser(clientUser);

        clientRepository.save(newClientRecord);
    }

    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new IllegalStateException("Can't find client of given id");
        }
        Client client = clientRepository.getById(id);
        clientRepository.deleteById(id);
        if(client.getUser() != null)
            appUserService.deleteUserById(client.getUser().getUserId());
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


    public Client getClientByEmail(String email) {
        return clientRepository.getByEmail(email);
    }

    public void changeStateOfUser(Long id) {
        if (clientRepository.existsById(id)){
            Client client = clientRepository.getById(id);
            appUserService.changeStateOfUser(client.getUser().getUserId());
        }
        else throw new IllegalStateException("Client with given id:" + id + " doesn't exist in database!");
    }
}

