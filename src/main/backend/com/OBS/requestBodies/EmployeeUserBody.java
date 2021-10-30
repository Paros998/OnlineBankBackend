package com.OBS.requestBodies;

import com.OBS.entity.Employee;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmployeeUserBody {
    Employee employee;
    UserCredentials userCredentials;

    public Employee getEmployee() {
        return employee;
    }

    public UserCredentials getUserCredentials() {
        return userCredentials;
    }
}
