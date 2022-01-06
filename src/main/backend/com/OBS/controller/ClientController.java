package com.OBS.controller;

import com.OBS.entity.Client;
import com.OBS.alternativeBodies.ClientUserBody;
import com.OBS.service.ClientService;
import lombok.AllArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.StartingWith;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Conjunction;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Or;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/clients")
public class ClientController {
    private final ClientService clientService;

    //TODO fix filtering
    @GetMapping(path = "/filtered")
    public List<Client> getClientsSorted(
            @Conjunction(value = {
                    @Or({
                            @Spec(path = "fullName", params = "personalNumber_personName", spec = StartingWith.class),
                            @Spec(path = "personalNumber", params = "personalNumber_personName", spec = StartingWith.class)
                    }),
            }, and = @Spec(path = "dateOfBirth", params = "birthDate", spec = Equal.class)) Specification<Client>filterClientSpec) {
        return clientService.getClients(filterClientSpec);
    }

    @GetMapping(path = "/latest/{days}")
    public List<Client> getLatestClients(@PathVariable("days") Integer days){return clientService.getLatestClients(days);}

    @GetMapping(path = "{id}")
    public Client getClient(@PathVariable Long id) {
        return clientService.getClient(id);
    }

    @PostMapping()
    public void addClient(@RequestBody ClientUserBody body) {
        clientService.addClient(body);
    }

    @PostMapping(path = "/only-client")
    public void addOnlyClient(@RequestBody Client body){clientService.addOnlyClient(body);}

    @PutMapping()
    public void updateClient(@RequestBody ClientUserBody body) {
        clientService.updateClient(body);
    }

    @DeleteMapping(path = "{id}")
    public void deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
    }

}
