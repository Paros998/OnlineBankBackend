package com.OBS.controller;

import com.OBS.auth.entity.AppUser;
import com.OBS.entity.*;
import com.OBS.service.*;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static com.OBS.auth.AppUserRole.*;
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
    private final TransferService transferService;
    private final CyclicalTransferService cyclicalTransferService;
    private final LoanService loanService;
    private final LoanRateService loanRateService;

    @GetMapping(path = "/credit-cards")
    public List<CreditCard> getCreditCards() {
        return creditCardService.getCreditCards();
    }

    @GetMapping(path = "/users")
    public List<AppUser> getAppUsers() {
        return appUserService.getUsers();
    }

    @GetMapping(path = "/clients")
    public List<Client> getClients() {
        return clientService.getClients();
    }

    @GetMapping(path = "/employees")
    public List<Employee> getEmployees() {
        return employeeService.getEmployees();
    }

    @GetMapping(path = "/announcements")
    public List<Announcement> getAnnouncements() {
        return announcementService.getAnnouncements();
    }

    @GetMapping(path = "/visits")
    public List<Visit> getVisits() {
        return visitService.getVisits();
    }

    @GetMapping(path = "/visits/unassigned")
    public List<Visit> getVisitsUnassigned() {
        return visitService.getVisitsUnassigned();
    }

    @GetMapping(path = "/orders")
    public List<Order> getOrders() {
        return orderService.getOrders(ADMIN.name());
    }

    @GetMapping(path = "/orders/for-employees")
    public List<Order> getOrdersForEmployees(){ return orderService.getOrders(EMPLOYEE.name());}

    @GetMapping(path = "/transfers")
    public List<Transfer> getTransfers(){ return transferService.getTransfers();}

    @GetMapping(path = "/cyclical-transfers")
    public List<CyclicalTransfer> getCyclicalTransfers(){return cyclicalTransferService.getTransfers();}

    @GetMapping(path = "/loans")
    public List<Loan> getLoans(){return loanService.getLoans();}

    @GetMapping(path = "/loans-rates")
    public List<LoanRate> getRates(){return loanRateService.getRates();}


    // TODO add rest of get endpoints when entities are implemented
}
