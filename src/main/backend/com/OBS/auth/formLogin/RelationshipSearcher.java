package com.OBS.auth.formLogin;

import com.OBS.service.ClientService;
import com.OBS.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RelationshipSearcher {
    private final ClientService clientService;
    private final EmployeeService employeeService;

    public Long searchIdByAppUserId(Long appUserId) {
        Long resultId = clientService.getClientIdByUserId(appUserId);

        if(resultId == null){
            resultId = employeeService.getEmployeeByUserId(appUserId);
        }
        return resultId;
    }
}
