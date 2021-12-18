package com.OBS.controller;

import com.OBS.entity.Employee;
import com.OBS.requestBodies.EmployeeUserBody;
import com.OBS.requestBodies.NamePersonalNum_BirthDateBody;
import com.OBS.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/filtered")
    public List<Employee> getEmployeesSorted(@RequestParam String birthDate,@RequestParam String personalNumber_personName) {
        return employeeService.getEmployees(personalNumber_personName,birthDate);
    }

    @GetMapping(path = "{id}")
    public Employee getEmployee(@PathVariable Long id) {
        return employeeService.getEmployee(id);
    }

    @PostMapping()
    public void addNewEmployee(@RequestBody EmployeeUserBody body) {
        employeeService.addEmployee(body);
    }

    @PutMapping()
    public void updateEmployee(@RequestBody EmployeeUserBody body) {
        employeeService.updateEmployee(body);
    }

    @DeleteMapping(path = "{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }

}
