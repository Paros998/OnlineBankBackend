package com.OBS.controller;

import com.OBS.entity.Client;
import com.OBS.requestBodies.ClientUserBody;
import com.OBS.requestBodies.NamePersonalNum_BirthDateBody;
import com.OBS.service.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/clients")
public class ClientController {
    private final ClientService clientService;

    @GetMapping(path = "/filtered")
    public List<Client> getClientsSorted(@RequestParam("birthDate") String birthDate, @RequestParam("personalNumber_personName") String personalNumber_personName) {
        return clientService.getClients(personalNumber_personName,LocalDate.parse(birthDate));
    }

    @GetMapping(path = "{id}")
    public Client getClient(@PathVariable Long id) {
        return clientService.getClient(id);
    }

    @PostMapping()
    public void addNewClient(@RequestBody ClientUserBody body) {
        clientService.addClient(body);
    }

    @PutMapping()
    public void updateClient(@RequestBody ClientUserBody body) {
        clientService.updateClient(body);
    }

    @DeleteMapping(path = "{id}")
    public void deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
    }

}
