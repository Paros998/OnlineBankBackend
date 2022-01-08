package com.OBS.service;

import com.OBS.auth.entity.AppUser;
import com.OBS.entity.Employee;
import com.OBS.repository.EmployeeRepository;
import com.OBS.alternativeBodies.EmployeeUserBody;
import com.OBS.alternativeBodies.UserCredentials;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final AppUserService appUserService;
    private final ClientEmployeeService clientEmployeeService;

    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> getEmployees(Specification<Employee> employeeSpecificationFilter) {
        return employeeRepository.findAll(employeeSpecificationFilter);
    }

    public Employee getEmployee(Long id) {
        return employeeRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("Employee with given id " + id + "doesn't exists in database")
        );
    }

    public void assignUserToEmployee(Employee employee, AppUser user){
        clientEmployeeService.assignUser(employee,user);
    }

    public void addEmployee(EmployeeUserBody body) {
         Employee employee = body.getEmployee();
        UserCredentials userCredentials = body.getUserCredentials();

        clientEmployeeService.checkForWithPN_IN(employee);

        clientEmployeeService.checkForEmployeeErrors(employee);
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

        clientEmployeeService.checkForWithPN_IN(newEmployeeRecord);

        clientEmployeeService.checkForErrorsBetweenOldEntities(currentEmployeeRecord,newEmployeeRecord);

        appUserService.updateAppUser(currentEmployeeRecord.getUser().getUserId(), newUserCredentials);

        if (!newUserCredentials.getEmail().isEmpty() && !newUserCredentials.getEmail().equals(currentEmployeeRecord.getEmail()))
            newEmployeeRecord.setEmail(newUserCredentials.getEmail());

        newEmployeeRecord.setUser(currentEmployeeRecord.getUser());

        employeeRepository.save(newEmployeeRecord);
    }

    @Transactional
    public void updateEmployee(Employee newEmployeeRecord){
        if (!employeeRepository.existsById(newEmployeeRecord.getEmployeeId())) {
            throw new IllegalStateException("Employee with given id:" + newEmployeeRecord.getEmployeeId() + " doesnt exist in database!");
        }

        Employee currentEmployeeRecord = employeeRepository.getById(newEmployeeRecord.getEmployeeId());

        clientEmployeeService.checkForWithPN_IN(newEmployeeRecord);

        clientEmployeeService.checkForErrorsBetweenOldEntities(currentEmployeeRecord,newEmployeeRecord);

        newEmployeeRecord.setUser(currentEmployeeRecord.getUser());

        if(!Objects.equals(newEmployeeRecord.getEmail(),currentEmployeeRecord.getEmail())){
            newEmployeeRecord.getUser().setEmail(newEmployeeRecord.getEmail());
            appUserService.updateAppUser(newEmployeeRecord.getUser());
        }

        employeeRepository.save(newEmployeeRecord);
    }

    public void deleteEmployee(Long id) {
        if (employeeRepository.existsById(id)){
            Employee employee = employeeRepository.getById(id);
            employeeRepository.deleteById(id);
            appUserService.deleteUserById(employee.getUser().getUserId());
        }
        else throw new IllegalStateException("Employee with given id:" + id + " doesn't exist in database!");
    }

    public Long getEmployeeByUserId(Long appUserId) {
        Employee employee = employeeRepository.getByUser(appUserService.getUser(appUserId));
        if (employee == null)
            return null;
        else return employee.getEmployeeId();
    }

    public Employee getEmployeeByEmail(String email) {
        return employeeRepository.getByEmail(email);
    }

    public void changeStateOfUser(Long id) {
        if (employeeRepository.existsById(id)){
            Employee employee = employeeRepository.getById(id);
            appUserService.changeStateOfUser(employee.getUser().getUserId());
        }
        else throw new IllegalStateException("Employee with given id:" + id + " doesn't exist in database!");
    }
}
