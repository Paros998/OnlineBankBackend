package com.OBS.controller;

import com.OBS.auth.entity.AppUser;
import com.OBS.requestBodies.UserCredentials;
import com.OBS.service.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class AppUserController {
    private final AppUserService appUserService;

    @GetMapping(path = "{id}")
    public AppUser getUser(@PathVariable("id") Long id) {
        return appUserService.getUser(id);
    }

    @PatchMapping(path = "{type}")
    public void forgotCredentials(@RequestBody String email, @PathVariable("type") String type) {
        if (Objects.equals(type, "login"))
            appUserService.remindLoginToEmail(email);
        else if (Objects.equals(type, "password"))
            appUserService.resetPasswordToEmail(email);
        else throw new IllegalStateException("Bad request type");
    }

    @PostMapping(path = "")
    public void addUser(@RequestBody UserCredentials userCredentials) {
        appUserService.createAppUser(userCredentials);
    }

    @DeleteMapping(path = "{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        appUserService.deleteUserById(id);
    }

}
