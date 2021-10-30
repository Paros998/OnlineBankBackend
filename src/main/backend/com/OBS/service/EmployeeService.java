package com.OBS.service;

import com.OBS.entity.Employee;
import com.OBS.repository.EmployeeRepository;
import com.OBS.requestBodies.EmployeeUserBody;
import com.OBS.requestBodies.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final AppUserService appUserService;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository,
                           AppUserService appUserService) {
        this.employeeRepository = employeeRepository;
        this.appUserService = appUserService;
    }

    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> getEmployees(String fullName, String personalNumber) {
        return employeeRepository.findAllByFullNameAndPersonalNumber(fullName, personalNumber);
//        List<Employee> employeeList = employeeRepository.findAll();
//        employeeList.removeIf(e -> !Objects.equals(e.getFullName(), fullName));
//        employeeList.removeIf(e -> !Objects.equals(e.getPersonalNumber(),personalNumber));
//        return  employeeList;
    }

    public Employee getEmployee(Long id) {
        return employeeRepository.findById(id).orElseThrow(IllegalAccessError::new);
    }

    public void addEmployee(EmployeeUserBody body) {
        Employee employee = body.getEmployee();
        UserCredentials userCredentials = body.getUserCredentials();

        if (employeeRepository.existsByPersonalNumber(employee.getPersonalNumber())) {
            throw new IllegalStateException("This Personal Number is already taken!");
        }
        if (employeeRepository.existsByIdentificationNumber(employee.getIdentificationNumber())) {
            throw new IllegalStateException("This Identification Number is already taken!");
        }
        employee.setUser(appUserService.createAppUser(userCredentials));
        employeeRepository.save(employee);

    }

    @Transactional
    public void updateEmployee(EmployeeUserBody body) {
        Employee newEmployeeRecord = body.getEmployee();
        UserCredentials newUserCredentials = body.getUserCredentials();
        if (!employeeRepository.existsById(newEmployeeRecord.getEmployeeId())) {
            throw new IllegalStateException("Employee with given id:" + newEmployeeRecord.getEmployeeId() + " doesnt exist in database!");
        }
        Employee currentEmployeeRecord = employeeRepository.getById(newEmployeeRecord.getEmployeeId());

        if(!currentEmployeeRecord.getPersonalNumber().equals(newEmployeeRecord.getPersonalNumber()))
            if (employeeRepository.existsByPersonalNumber(newEmployeeRecord.getPersonalNumber())) {
                throw new IllegalStateException("This Personal Number is already taken!");
            }
        if(!currentEmployeeRecord.getIdentificationNumber().equals(newEmployeeRecord.getIdentificationNumber()))
            if (employeeRepository.existsByIdentificationNumber(newEmployeeRecord.getIdentificationNumber())) {
                throw new IllegalStateException("This Identification Number is already taken!");
            }

        appUserService.updateAppUser(currentEmployeeRecord.getUser().getUserId(), newUserCredentials);

        if(!newUserCredentials.getEmail().isEmpty() && !newUserCredentials.getEmail().equals(currentEmployeeRecord.getEmail()))
            newEmployeeRecord.setEmail(newUserCredentials.getEmail());

        newEmployeeRecord.setUser(currentEmployeeRecord.getUser());

        employeeRepository.save(newEmployeeRecord);
    }

    public void deleteEmployee(Long id) {
        if (employeeRepository.existsById(id))
            employeeRepository.deleteById(id);
        else throw new IllegalStateException("Employee with given id:" + id + " doesnt exist in database!");
    }
}
