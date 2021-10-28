package com.OBS.controller;

import com.OBS.entity.Employee;
import com.OBS.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> getEmployees() {
        return employeeService.getEmployees();
    }

    @GetMapping(path = "?{fullName}&?{personalNumber}")
    public List<Employee> getEmployeesSorted(@PathVariable String fullName, @PathVariable String personalNumber) {
        return employeeService.getEmployees(fullName, personalNumber);
    }

    @GetMapping(path = "{id}")
    public Employee getEmployee(@PathVariable Long id) {
        return employeeService.getEmployee(id);
    }

    @PostMapping(path = "{username}&{password}&{email}")
    public void addNewEmployee(@RequestBody Employee employee,
                               @PathVariable String username,
                               @PathVariable String password,
                               @PathVariable String email) {
        employeeService.addEmployee(employee, username, password, email);
    }

    @PutMapping(path = "{username}&{password}&{email}")
    public void updateEmployee(@RequestBody Employee employee,
                               @PathVariable String username,
                               @PathVariable String password,
                               @PathVariable String email) {
        employeeService.updateEmployee(employee, username, password, email);
    }

    @DeleteMapping(path = "{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }

}
