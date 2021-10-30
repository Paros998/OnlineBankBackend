package com.OBS.service;

import com.OBS.auth.entity.AppUser;
import com.OBS.repository.AppUserRepository;
import com.OBS.requestBodies.UserCredentials;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {
    private final static String USER_NOT_FOUND = "User with username %s not found";

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, username))
        );
    }

    public AppUser createAppUser(UserCredentials userCredentials) {
        if (appUserRepository.existsByEmail(userCredentials.getEmail())) {
            throw new IllegalStateException("This email is already taken!");
        }
        if (appUserRepository.existsByUsername(userCredentials.getUsername())) {
            throw new IllegalStateException("This username is already taken!");
        }
        userCredentials.setPassword(bCryptPasswordEncoder.encode(userCredentials.getPassword()));
        AppUser appUser = new AppUser(userCredentials, false, true);
        appUserRepository.save(appUser);
        return appUser;
    }

    public List<AppUser> getUsers() {
        return  appUserRepository.findAll();
    }

    public void sendCredentialsToEmail(String email){
        if(appUserRepository.existsByEmail(email)){

        }
        else
            throw new IllegalStateException("User with given email doesn't exist!");

    }

    @Transactional
    public void updateAppUser(Long id, UserCredentials userCredentials) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, userCredentials.getUsername()))
        );
        if(!appUser.getEmail().equals(userCredentials.getEmail()))
            if (appUserRepository.existsByEmail(userCredentials.getEmail())) {
                throw new IllegalStateException("This email is already taken!");
            }
        if(!appUser.getUsername().equals(userCredentials.getUsername()))
            if (appUserRepository.existsByUsername(userCredentials.getUsername())) {
                throw new IllegalStateException("This username is already taken!");
            }
        if (!userCredentials.getUsername().isEmpty())
            appUser.setUsername(userCredentials.getUsername());
        if (!userCredentials.getPassword().isEmpty()){
            userCredentials.setPassword(bCryptPasswordEncoder.encode(userCredentials.getPassword()));
            appUser.setPassword(userCredentials.getPassword());
        }
        if (!userCredentials.getEmail().isEmpty())
            appUser.setEmail(userCredentials.getEmail());
        appUserRepository.save(appUser);
    }


    public void deleteUserById(Long id) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("User with given id "+id+" doesn't exist in database!")
        );
        appUserRepository.deleteById(id);
    }
}
