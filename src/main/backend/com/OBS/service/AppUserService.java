package com.OBS.service;

import com.OBS.email.EmailService;
import com.OBS.auth.entity.AppUser;
import com.OBS.email.EmailTemplates;
import com.OBS.entity.Client;
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
import java.util.Random;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {
    private final static String USER_NOT_FOUND = "User with username %s not found";

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AppUserRepository appUserRepository;

    private final EmailService emailService;
    private final EmailTemplates emailTemplates;

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
        return appUserRepository.findAll();
    }

    public AppUser getUser(Long id) {
        return appUserRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("User with given id doesn't exist!")
        );
    }

    public AppUser getUser(String username) {
        return appUserRepository.getByUsername(username);
    }

    public void remindLoginToEmail(String email) {
        if (appUserRepository.existsByEmail(email)) {
            AppUser appUser = appUserRepository.getByEmail(email);

            emailService.send(
                    appUser.getEmail(),
                    emailTemplates.emailTemplateForLoginReminder(appUser.getUsername()),
                    "Remember your login credentials"
            );

        } else
            throw new IllegalStateException("User with given email doesn't exist!");
    }

    @Transactional
    public void resetPasswordToEmail(String email) {
        if (appUserRepository.existsByEmail(email)) {
            Random random = new Random();
            AppUser appUser = appUserRepository.getByEmail(email);
            StringBuilder newPassword = new StringBuilder();
            for (int i = 0; i < 25; i++)
                newPassword.append((char) (random.nextInt(87) + 33));

            appUser.setPassword(bCryptPasswordEncoder.encode(newPassword.toString()));

            emailService.send(
                    appUser.getEmail(),
                    emailTemplates.emailTemplateForPasswordReset(appUser.getUsername(), newPassword.toString()),
                    "Remember your login credentials"
            );
        } else
            throw new IllegalStateException("User with given email doesn't exist!");

    }

    @Transactional
    public void updateAppUser(AppUser user){
        appUserRepository.save(user);
    }

    @Transactional
    public void updateAppUser(Long id, UserCredentials userCredentials) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, userCredentials.getUsername()))
        );
        if (!appUser.getEmail().equals(userCredentials.getEmail()))
            if (appUserRepository.existsByEmail(userCredentials.getEmail())) {
                throw new IllegalStateException("This email is already taken!");
            }
        if (!appUser.getUsername().equals(userCredentials.getUsername()))
            if (appUserRepository.existsByUsername(userCredentials.getUsername())) {
                throw new IllegalStateException("This username is already taken!");
            }
        if (!userCredentials.getUsername().isEmpty())
            appUser.setUsername(userCredentials.getUsername());
        if (!userCredentials.getPassword().isEmpty()) {
            userCredentials.setPassword(bCryptPasswordEncoder.encode(userCredentials.getPassword()));
            appUser.setPassword(userCredentials.getPassword());
        }
        if (!userCredentials.getEmail().isEmpty())
            appUser.setEmail(userCredentials.getEmail());
        appUserRepository.save(appUser);
    }


    public void deleteUserById(Long id) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("User with given id " + id + " doesn't exist in database!")
        );
        appUserRepository.deleteById(id);
    }

}
