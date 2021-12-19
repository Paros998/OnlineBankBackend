package com.OBS.controller;

import com.OBS.entity.Client;
import com.OBS.entity.Employee;
import com.OBS.requestBodies.EmployeeUserBody;
import com.OBS.requestBodies.NamePersonalNum_BirthDateBody;
import com.OBS.service.EmployeeService;
import lombok.AllArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/filtered")
    public List<Employee> getEmployeesSorted(
            @And({
                    @Spec(path = "dateOfBirth", params = "birthDate", spec = Equal.class),
                    @Spec(path = "fullName", params = "personalNumber_personName", spec = Equal.class),
                    @Spec(path = "personalNumber", params = "personalNumber_personName", spec = Equal.class),
            }) Specification<Employee> filterEmployeeSpec) {
        return employeeService.getEmployees(filterEmployeeSpec);
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
