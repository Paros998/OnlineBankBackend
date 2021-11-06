package com.OBS.service;

import com.OBS.repository.ClientRepository;
import com.OBS.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClientEmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;

    public boolean existsByPersonalNumber(String personalNumber) {
        return employeeRepository.existsByPersonalNumber(personalNumber) && clientRepository.existsByPersonalNumber(personalNumber);
    }

    public boolean existsByIdentificationNumber(String identificationNumber) {
        return employeeRepository.existsByIdentificationNumber(identificationNumber) && clientRepository.existsByIdentificationNumber(identificationNumber);
    }
}
