package com.OBS.controller;

import com.OBS.entity.Visit;
import com.OBS.alternativeBodies.AssignEmployeeToVisitBody;
import com.OBS.service.VisitService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/visits")
@AllArgsConstructor
public class VisitController {
    private final VisitService visitService;

    @GetMapping(path = "employee/{id}")
    public List<Visit> getEmployeeVisits(@PathVariable("id") Long id) {
        return visitService.getEmployeeVisits(id);
    }

    @GetMapping(path = "{id}")
    public Visit getVisit(@PathVariable("id") Long id) {
        return visitService.getVisit(id);
    }

    @PostMapping()
    public void addVisit(@RequestBody Visit visit) {
        visitService.addVisit(visit);
    }

    @PutMapping(path = "{id}/closed")
    public void updateVisit(@PathVariable("id") Long id){
        visitService.setInactive(id);
    }

    @PutMapping(path = "{id}")
    public void addEmployeeToVisit(@PathVariable("id") Long id,@RequestBody AssignEmployeeToVisitBody body ){
        visitService.setEmployee(id,body.getEmployeeId());
    }

    @DeleteMapping(path = "{id}")
    public void deleteVisit(@PathVariable Long id){
        visitService.deleteVisit(id);
    }
}
