package com.OBS.service.interfaces.systemFacade;

import com.OBS.entity.Employee;
import com.sun.istack.NotNull;
import org.springframework.stereotype.Service;

@Service
public interface EmployeeServiceFacade {
    Employee getEmployeeByEmail(String email);
    void updateEmployee(@NotNull Employee newEmployeeRecord);
}
