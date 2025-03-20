package com.example.medicalclinic.controller;

import com.example.medicalclinic.model.CreateVisitCommand;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.model.dto.VisitDTO;
import com.example.medicalclinic.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


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
}
