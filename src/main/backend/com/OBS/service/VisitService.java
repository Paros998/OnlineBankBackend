package com.OBS.service;

import com.OBS.entity.Visit;
import com.OBS.repository.VisitRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;

    public Visit getVisit(Long id) {
        return visitRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("Visit by given id:" + id + " doesn't exists in database")
        );
    }

    public void addVisit(Visit visit) {
        visitRepository.save(visit);
    }

    public List<Visit> getVisits() {
        return visitRepository.findAll();
    }
}
