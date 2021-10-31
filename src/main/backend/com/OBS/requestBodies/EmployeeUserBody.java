package com.OBS.requestBodies;

import com.OBS.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeUserBody {
    Employee employee;
    UserCredentials userCredentials;

}
