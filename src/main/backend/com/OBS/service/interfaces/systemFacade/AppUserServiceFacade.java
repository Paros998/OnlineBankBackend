package com.OBS.service.interfaces.systemFacade;

import com.OBS.alternativeBodies.UserCredentials;
import com.OBS.auth.entity.AppUser;
import com.sun.istack.NotNull;
import org.springframework.stereotype.Service;

@Service
public interface AppUserServiceFacade {
    AppUser createAppUser(@NotNull UserCredentials userCredentials);
    void updateAppUser(AppUser user);
    void updateAppUser(Long id, @NotNull UserCredentials userCredentials);
    AppUser getUserByEmail(String email);
}
