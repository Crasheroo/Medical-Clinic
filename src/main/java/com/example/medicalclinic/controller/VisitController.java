package com.example.medicalclinic.controller;

import com.example.medicalclinic.model.CreateVisitCommand;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.model.dto.VisitDTO;
import com.example.medicalclinic.model.entity.Visit;
import com.example.medicalclinic.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


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
    public VisitDTO bookVisit(@RequestParam Long visitId, @RequestParam Long patientId) {
        return visitService.bookVisit(visitId, patientId);
    }

    @GetMapping
    public PageableContentDTO<VisitDTO> getVisits(Pageable pageable) {
        return visitService.getVisits(pageable);
    }

    @PostMapping("/{id}/reserve")
    public Visit reserveVisit(@PathVariable Long id, @RequestParam String patientEmail) {
        return visitService.reserveVisit(id, patientEmail);
    }

    @GetMapping("/my-visits")
    public List<VisitDTO> getMyVisits(@RequestParam String patientEmail) {
        return visitService.getVisitsByPatient(patientEmail);
    }

    @GetMapping("/doctor/{doctorId}/available")
    public List<VisitDTO> getDoctorAvailableVisits(@PathVariable Long doctorId) {
        return visitService.getAvailableVisitsByDoctor(doctorId);
    }

    @GetMapping("/available/by-specialty")
    public List<VisitDTO> getAvailableBySpecialtyAndDate(
            @RequestParam String specialty,
            @RequestParam LocalDateTime date) {
        return visitService.getAvailableVisitsBySpecialtyAndDay(specialty, date);
    }
}
