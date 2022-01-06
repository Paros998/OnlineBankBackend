package com.OBS.alternativeBodies;

import com.OBS.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeUserBody {
    Employee employee;
    UserCredentials userCredentials;

}
