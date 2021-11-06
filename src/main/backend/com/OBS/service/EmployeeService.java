package com.OBS.service;

import com.OBS.entity.Employee;
import com.OBS.repository.EmployeeRepository;
import com.OBS.requestBodies.EmployeeUserBody;
import com.OBS.requestBodies.UserCredentials;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final AppUserService appUserService;
    private final ClientEmployeeService clientEmployeeService;

    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> getEmployees(String fullName, String personalNumber) {
        List<Employee> employeeList = employeeRepository.findAll();
        employeeList.removeIf(e -> !e.getFullName().contains(fullName));
        employeeList.removeIf(e -> !e.getPersonalNumber().startsWith(personalNumber));
        return employeeList;
    }

    public Employee getEmployee(Long id) {
        return employeeRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("Employee with given id " + id + "doesn't exists in database")
        );
    }

    public void addEmployee(EmployeeUserBody body) {
        Employee employee = body.getEmployee();
        UserCredentials userCredentials = body.getUserCredentials();

        if (clientEmployeeService.existsByPersonalNumber(employee.getPersonalNumber())) {
            throw new IllegalStateException("This Personal Number is already taken!");
        }

        if (clientEmployeeService.existsByIdentificationNumber(employee.getIdentificationNumber())) {
            throw new IllegalStateException("This Personal Number is already taken!");
        }

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

        if (clientEmployeeService.existsByPersonalNumber(newEmployeeRecord.getPersonalNumber())) {
            throw new IllegalStateException("This Personal Number is already taken!");
        }

        if (clientEmployeeService.existsByIdentificationNumber(newEmployeeRecord.getIdentificationNumber())) {
            throw new IllegalStateException("This Personal Number is already taken!");
        }

        if (!currentEmployeeRecord.getPersonalNumber().equals(newEmployeeRecord.getPersonalNumber()))
            if (employeeRepository.existsByPersonalNumber(newEmployeeRecord.getPersonalNumber())) {
                throw new IllegalStateException("This Personal Number is already taken!");
            }
        if (!currentEmployeeRecord.getIdentificationNumber().equals(newEmployeeRecord.getIdentificationNumber()))
            if (employeeRepository.existsByIdentificationNumber(newEmployeeRecord.getIdentificationNumber())) {
                throw new IllegalStateException("This Identification Number is already taken!");
            }

        appUserService.updateAppUser(currentEmployeeRecord.getUser().getUserId(), newUserCredentials);

        if (!newUserCredentials.getEmail().isEmpty() && !newUserCredentials.getEmail().equals(currentEmployeeRecord.getEmail()))
            newEmployeeRecord.setEmail(newUserCredentials.getEmail());

        newEmployeeRecord.setUser(currentEmployeeRecord.getUser());

        employeeRepository.save(newEmployeeRecord);
    }

    public void deleteEmployee(Long id) {
        if (employeeRepository.existsById(id))
            employeeRepository.deleteById(id);
        else throw new IllegalStateException("Employee with given id:" + id + " doesnt exist in database!");
    }

    public Long getEmployeeByUserId(Long appUserId) {
        Employee employee = employeeRepository.getByUser(appUserService.getUser(appUserId));
        if (employee == null)
            return null;
        else return employee.getEmployeeId();
    }
}
