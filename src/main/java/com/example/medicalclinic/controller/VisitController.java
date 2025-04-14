package com.example.medicalclinic.controller;

import com.example.medicalclinic.model.BookVisitCommand;
import com.example.medicalclinic.model.CreateVisitCommand;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.model.dto.VisitDTO;
import com.example.medicalclinic.model.dto.VisitFilterDTO;
import com.example.medicalclinic.model.entity.Visit;
import com.example.medicalclinic.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
@RequestMapping("/visits")
public class VisitController {
    private final VisitService visitService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitDTO createVisit(@RequestBody CreateVisitCommand request) {
        return visitService.createVisit(request.doctorId(), request.startTime(), request.endTime());
    }

    @PostMapping("/book")
    public VisitDTO bookVisit(@RequestBody BookVisitCommand request) {
        return visitService.bookVisit(request.visitId(), request.patientid());
    }

    @GetMapping
    public PageableContentDTO<VisitDTO> getVisits(VisitFilterDTO filter , Pageable pageable) {
        return visitService.getVisits(filter, pageable);
    }

    @PostMapping("/{id}/reserve")
    public Visit reserveVisit(@PathVariable Long id, @RequestParam String patientEmail) {
        return visitService.reserveVisit(id, patientEmail);
    }

    @DeleteMapping("/cancel/{id}")
    public void cancelVisit(@PathVariable Long id, @RequestParam String doctorEmail) {
        visitService.cancelVisit(id, doctorEmail);
    }
}
