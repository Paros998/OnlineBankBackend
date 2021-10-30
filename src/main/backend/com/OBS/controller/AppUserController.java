package com.OBS.controller;

import com.OBS.auth.entity.AppUser;
import com.OBS.requestBodies.UserCredentials;
import com.OBS.service.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class AppUserController {
    private final AppUserService appUserService;

    @GetMapping(path = "")
    public List<AppUser> getUsers(){
        return appUserService.getUsers();
    }

    @GetMapping(path = "{email}")
    public void forgotCredentials(@PathVariable("email") String email){
        appUserService.sendCredentialsToEmail(email);
    }

    @PostMapping(path = "")
    public void addUser(@RequestBody UserCredentials userCredentials){
        appUserService.createAppUser(userCredentials);
    }

    @DeleteMapping(path = "{id}")
    public void deleteUser(@PathVariable("id") Long id){
        appUserService.deleteUserById(id);
    }

}
