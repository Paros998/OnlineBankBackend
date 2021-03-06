package com.OBS.controller;

import com.OBS.alternativeBodies.EmployeeUserBody;
import com.OBS.entity.Client;
import com.OBS.entity.Employee;
import com.OBS.enums.SearchOperation;
import com.OBS.searchers.SearchCriteria;
import com.OBS.searchers.specificators.Specifications;
import com.OBS.service.EmployeeService;
import lombok.AllArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.StartingWith;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Conjunction;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Or;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/filtered")
    public List<Employee> getEmployeesSorted(
            @Conjunction(value = {
                    @Or({
                            @Spec(path = "fullName", params = "personalNumber_personName", spec = StartingWith.class),
                            @Spec(path = "personalNumber", params = "personalNumber_personName", spec = StartingWith.class)
                    }),
            }, and = @Spec(path = "dateOfBirth", params = "birthDate", spec = Equal.class)) Specification<Employee> filterEmployeeSpec) {
        return employeeService.getEmployees(filterEmployeeSpec);
    }

    @GetMapping(path = "/active")
    public List<Employee> getActiveEmployees(){
        Specifications<Employee> findActiveEmployees = new Specifications<Employee>()
                .add(new SearchCriteria("enabled",true, SearchOperation.EQUAL_JOIN_USER));
        return employeeService.getEmployees(findActiveEmployees);
    }

    @GetMapping(path = "/inactive")
    public List<Employee> getInactiveEmployees(){
        Specifications<Employee> findInactiveEmployees = new Specifications<Employee>()
                .add(new SearchCriteria("enabled",false, SearchOperation.EQUAL_JOIN_USER));
        return employeeService.getEmployees(findInactiveEmployees);
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

    @PutMapping(path = "{id}")
    public void changeStateOfUser(@PathVariable Long id){employeeService.changeStateOfUser(id);}

    @DeleteMapping(path = "{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }

}
