package com.OBS.service;

import com.OBS.auth.entity.AppUser;
import com.OBS.entity.Client;
import com.OBS.entity.Employee;
import com.OBS.repository.ClientRepository;
import com.OBS.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class ClientEmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;
    private final AppUserService appUserService;

    @Transactional
    public void assignUser(Client client, AppUser user){
        user.setEmail(client.getEmail());
        client.setUser(user);

        appUserService.updateAppUser(user);
        clientRepository.save(client);
    }

    @Transactional
    public void assignUser(Employee employee, AppUser user){
        user.setEmail(employee.getEmail());
        employee.setUser(user);

        appUserService.updateAppUser(user);
        employeeRepository.save(employee);
    }

    public void checkForWithPN_IN(Client client) {
        if (existsByPersonalNumber(client.getPersonalNumber())) {
            throw new IllegalStateException("This Personal Number is already taken!");
        }

        if (existsByIdentificationNumber(client.getIdentificationNumber())) {
            throw new IllegalStateException("This Personal Number is already taken!");
        }
    }

    public void checkForWithPN_IN(Employee employee) {
        if (existsByPersonalNumber(employee.getPersonalNumber())) {
            throw new IllegalStateException("This Personal Number is already taken!");
        }

        if (existsByIdentificationNumber(employee.getIdentificationNumber())) {
            throw new IllegalStateException("This Personal Number is already taken!");
        }
    }

    private boolean existsByPersonalNumber(String personalNumber) {
        return employeeRepository.existsByPersonalNumber(personalNumber) && clientRepository.existsByPersonalNumber(personalNumber);
    }

    private boolean existsByIdentificationNumber(String identificationNumber) {
        return employeeRepository.existsByIdentificationNumber(identificationNumber) && clientRepository.existsByIdentificationNumber(identificationNumber);
    }

    public void checkForClientErrors(Client client) {
        if (clientRepository.existsByPersonalNumber(client.getPersonalNumber())) {
            throw new IllegalStateException("This Personal Number is already taken!");
        }
        if (clientRepository.existsByIdentificationNumber(client.getIdentificationNumber())) {
            throw new IllegalStateException("This Identification Number is already taken!");
        }
        if (clientRepository.existsByAccountNumber(client.getAccountNumber())) {
            throw new IllegalStateException("This Account Number is already taken!");
        }
    }

    public void checkForEmployeeErrors(Employee employee) {
        if (employeeRepository.existsByPersonalNumber(employee.getPersonalNumber())) {
            throw new IllegalStateException("This Personal Number is already taken!");
        }
        if (employeeRepository.existsByIdentificationNumber(employee.getIdentificationNumber())) {
            throw new IllegalStateException("This Identification Number is already taken!");
        }
    }

    public void checkForErrorsBetweenOldEntities(Client currentClientRecord, Client newClientRecord) {
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
    }
    public void checkForErrorsBetweenOldEntities(Employee currentEmployeeRecord, Employee newEmployeeRecord) {
        if (!currentEmployeeRecord.getPersonalNumber().equals(newEmployeeRecord.getPersonalNumber())) {
            if (employeeRepository.existsByPersonalNumber(newEmployeeRecord.getPersonalNumber()))
                throw new IllegalStateException("This Personal Number is already taken!");
        }

        if (!currentEmployeeRecord.getIdentificationNumber().equals(newEmployeeRecord.getIdentificationNumber())) {
            if (employeeRepository.existsByIdentificationNumber(newEmployeeRecord.getIdentificationNumber()))
                throw new IllegalStateException("This Identification Number is already taken!");
        }

    }
}
