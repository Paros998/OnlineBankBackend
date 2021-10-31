package com.OBS.controller;

import com.OBS.entity.CreditCard;
import com.OBS.service.*;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/dictionary")
@AllArgsConstructor
public class DictionaryController {
    private final CreditCardService creditCardService;
    private final AppUserService appUserService;
    private final ClientService clientService;
    private final EmployeeService employeeService;
    private final AnnouncementService announcementService;

    @GetMapping(path = "/credit-cards")
    public List<CreditCard> getCreditCards() {
        return creditCardService.getCreditCards();
    }

    // TODO add rest of get endpoints
}
