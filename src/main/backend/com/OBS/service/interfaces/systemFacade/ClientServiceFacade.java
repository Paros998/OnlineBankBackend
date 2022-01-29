package com.OBS.service.interfaces.systemFacade;

import com.OBS.entity.Client;
import com.sun.istack.NotNull;
import org.springframework.stereotype.Service;

@Service
public interface ClientServiceFacade {
    Client getClientByEmail(String email);
    void updateClient(@NotNull Client newClientRecord);
}
