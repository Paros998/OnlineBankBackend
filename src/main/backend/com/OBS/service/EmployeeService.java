package com.OBS.service;

import com.OBS.auth.AppUserRole;
import com.OBS.entity.Employee;
import com.OBS.repository.AppUserRepository;
import com.OBS.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final AppUserRepository appUserRepository;
    private final AppUserService appUserService;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository,
                           AppUserRepository appUserRepository,
                           AppUserService appUserService) {
        this.employeeRepository = employeeRepository;
        this.appUserRepository = appUserRepository;
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

    public void addEmployee(Employee employee, String username, String password, String email) {
        if (appUserRepository.existsByEmail(employee.getEmail())) {
            throw new IllegalStateException("This email is already taken!");
        }
        if (appUserRepository.existsByUsername(username)) {
            throw new IllegalStateException("This username is already taken!");
        }
        if (employeeRepository.existsByPersonalNumber(employee.getPersonalNumber())) {
            throw new IllegalStateException("This Personal Number is already taken!");
        }
        if (employeeRepository.existsByIdentificationNumber(employee.getIdentificationNumber())) {
            throw new IllegalStateException("This Identification Number is already taken!");
        }
        employee.setUser(appUserService.createAppUser(username, password, email, AppUserRole.EMPLOYEE));
        employeeRepository.save(employee);

    }

    @Transactional
    public void updateEmployee(Employee employee, String username, String password, String email) {
        if (!employeeRepository.existsById(employee.getEmployeeId())) {
            throw new IllegalStateException("Employee with given id:" + employee.getEmployeeId() + " doesnt exist in database!");
        }
        if (appUserRepository.existsByEmail(employee.getEmail())) {
            throw new IllegalStateException("This email is already taken!");
        }
        if (appUserRepository.existsByUsername(username)) {
            throw new IllegalStateException("This username is already taken!");
        }
        if (employeeRepository.existsByPersonalNumber(employee.getPersonalNumber())) {
            throw new IllegalStateException("This Personal Number is already taken!");
        }
        if (employeeRepository.existsByIdentificationNumber(employee.getIdentificationNumber())) {
            throw new IllegalStateException("This Identification Number is already taken!");
        }
        if (appUserRepository.existsById(employee.getUser().getUserId())) {
            throw new IllegalStateException("Employee User doesnt exists in database");
        }
        appUserService.updateAppUser(employee.getUser().getUserId(), username, password, email);
        employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        if (employeeRepository.existsById(id))
            employeeRepository.deleteById(id);
        else throw new IllegalStateException("Employee with given id:" + id + " doesnt exist in database!");
    }
}
