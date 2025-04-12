package com.example.medicalclinic.controller;

import com.example.medicalclinic.model.CreateVisitCommand;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.model.dto.VisitDTO;
import com.example.medicalclinic.model.dto.VisitFilterDTO;
import com.example.medicalclinic.model.entity.Visit;
import com.example.medicalclinic.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public PageableContentDTO<VisitDTO> getVisits(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "false") boolean onlyAvailable,
            @RequestParam(required = false) String patientEmail,
            Pageable pageable) {

        VisitFilterDTO filter = VisitFilterDTO.builder()
                .doctorId(doctorId)
                .speciality(specialty)
                .date(date)
                .onlyAvailable(onlyAvailable)
                .patientEmail(patientEmail)
                .build();

        return visitService.getVisits(filter, pageable);
    }

    @PostMapping("/{id}/reserve")
    public Visit reserveVisit(@PathVariable Long id, @RequestParam String patientEmail) {
        return visitService.reserveVisit(id, patientEmail);
    }
}
