package com.OBS.controller;

import com.OBS.auth.entity.AppUser;
import com.OBS.entity.*;
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
    private final VisitService visitService;
    private final OrderService orderService;

    @GetMapping(path = "/credit-cards")
    public List<CreditCard> getCreditCards() {
        return creditCardService.getCreditCards();
    }

    @GetMapping(path = "/users")
    public List<AppUser> getAppUsers(){return appUserService.getUsers();}

    @GetMapping(path = "/clients")
    public List<Client> getClients(){return clientService.getClients();}

    @GetMapping(path = "/employees")
    public List<Employee> getEmployees(){return employeeService.getEmployees();}

    @GetMapping(path = "/announcements")
    public List<Announcement> getAnnouncements(){return announcementService.getAnnouncements();}

    @GetMapping(path = "/visits")
    public List<Visit> getVisits(){return visitService.getVisits();}

    @GetMapping(path = "/orders")
    public List<Order> getOrders(){return orderService.getOrders();}


    // TODO add rest of get endpoints when entities are implemented
}
