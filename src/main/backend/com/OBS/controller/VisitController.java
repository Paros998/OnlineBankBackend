package com.OBS.controller;

import com.OBS.entity.Visit;
import com.OBS.service.VisitService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/visits")
@AllArgsConstructor
public class VisitController {
    private final VisitService visitService;

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

    @DeleteMapping(path = "{id}")
    public void deleteVisit(@PathVariable Long id){
        visitService.deleteVisit(id);
    }
}
