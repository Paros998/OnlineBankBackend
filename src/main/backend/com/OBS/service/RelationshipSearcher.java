package com.OBS.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RelationshipSearcher {
    private final ClientService clientService;
    private final EmployeeService employeeService;

    public Long searchIdByAppUserId(Long appUserId) {
        Long resultId = clientService.getClientIdByUserId(appUserId);

        if (resultId == null) {
            resultId = employeeService.getEmployeeByUserId(appUserId);
        }
        return resultId;
    }
}
