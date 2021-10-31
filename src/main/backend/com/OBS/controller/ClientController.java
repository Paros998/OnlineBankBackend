package com.OBS.controller;

import com.OBS.entity.Client;
import com.OBS.entity.Employee;
import com.OBS.requestBodies.ClientUserBody;
import com.OBS.requestBodies.EmployeeUserBody;
import com.OBS.service.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/clients")
public class ClientController {
    private final ClientService clientService;

    @GetMapping
    public List<Client> getClients() {
        return clientService.getClients();
    }

    @GetMapping(path = "?{fullName}&?{personalNumber}")
    public List<Client> getClientsSorted(@PathVariable String fullName, @PathVariable String personalNumber) {
        return clientService.getClients(fullName, personalNumber);
    }

    @GetMapping(path = "{id}")
    public Client getClient(@PathVariable Long id) {
        return clientService.getClient(id);
    }

    @PostMapping()
    public void addNewEmployee(@RequestBody ClientUserBody body) {
        clientService.addClient(body);
    }

    @PutMapping()
    public void updateEmployee(@RequestBody ClientUserBody body) {
        clientService.updateClient(body);
    }

    @DeleteMapping(path = "{id}")
    public void deleteEmployee(@PathVariable Long id) {
        clientService.deleteClient(id);
    }

}
