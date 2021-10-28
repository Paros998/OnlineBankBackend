package com.OBS.service;

import com.OBS.auth.AppUser;
import com.OBS.auth.AppUserRole;
import com.OBS.repository.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {
    private final static String USER_NOT_FOUND = "User with username %s not found";

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, username))
        );
    }

    public AppUser createAppUser(String username, String password, String email, AppUserRole userRole) {
        AppUser appUser = new AppUser(username, password, email, userRole, false, true);
        appUserRepository.save(appUser);
        return appUser;
    }

    @Transactional
    public void updateAppUser(Long id, String username, String password, String email) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, username))
        );
        if (!username.isEmpty())
            appUser.setUsername(username);
        if (!password.isEmpty())
            appUser.setPassword(password);
        if (!email.isEmpty())
            appUser.setEmail(email);
    }
}
