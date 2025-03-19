package com.example.medicalclinic.controller;

import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.model.dto.VisitDTO;
import com.example.medicalclinic.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/visits")
public class VisitController {
    private final VisitService visitService;

    @PostMapping("/create")
    public void createVisit(@RequestParam Long doctorId, @RequestParam LocalDateTime startTime, @RequestParam LocalDateTime endTime) {
        visitService.createVisit(doctorId, startTime, endTime);
    }

    @PostMapping("/book")
    public void bookVisit(@RequestParam Long visitId, @RequestParam Long patientId) {
        visitService.bookVisit(visitId, patientId);
    }

    @GetMapping
    public PageableContentDTO<VisitDTO> getVisits(Pageable pageable) {
        return visitService.getVisits(pageable);
    }
}
