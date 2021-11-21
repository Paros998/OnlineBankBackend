package com.OBS.controller;

import com.OBS.entity.Client;
import com.OBS.requestBodies.ClientUserBody;
import com.OBS.requestBodies.NameAndPersonalNumBody;
import com.OBS.service.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/clients")
public class ClientController {
    private final ClientService clientService;

    @GetMapping(path = "/filtered")
    public List<Client> getClientsSorted(@RequestBody NameAndPersonalNumBody body) {
        return clientService.getClients(body);
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
