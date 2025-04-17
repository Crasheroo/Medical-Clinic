package com.example.medicalclinic.controller;

import com.example.medicalclinic.model.BookVisitCommand;
import com.example.medicalclinic.model.CreateVisitCommand;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.model.dto.VisitDTO;
import com.example.medicalclinic.model.dto.VisitFilterDTO;
import com.example.medicalclinic.model.entity.Visit;
import com.example.medicalclinic.service.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Visit operations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/visits")
public class VisitController {
    private final VisitService visitService;

    @Operation(summary = "Create visit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Visit created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = VisitDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Visit already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitDTO createVisit(@RequestBody CreateVisitCommand request) {
        return visitService.createVisit(request.doctorId(), request.startTime(), request.endTime());
    }

    @Operation(summary = "Book visit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Visit found and booked",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = VisitDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Visit not found")
    })
    @PostMapping("/book")
    public VisitDTO bookVisit(@RequestBody BookVisitCommand request) {
        return visitService.bookVisit(request.visitId(), request.patientId());
    }

    @Operation(summary = "Get visits")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Visits found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableContentDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Visits not found")
    })
    @GetMapping
    public PageableContentDTO<VisitDTO> getVisits(@ParameterObject VisitFilterDTO filter , Pageable pageable) {
        return visitService.getVisits(filter, pageable);
    }

    @Operation(summary = "Reserve visit by id and patient email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Visit and patient found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Visit.class))}),
            @ApiResponse(responseCode = "404", description = "Visit or patient not found")
    })
    @PostMapping("/{id}/reserve")
    public Visit reserveVisit(@PathVariable Long id, @RequestParam String patientEmail) {
        return visitService.reserveVisit(id, patientEmail);
    }

    @Operation(summary = "Cancel visit by visit id and doctor email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Visit and doctor found, visit canceled"),
            @ApiResponse(responseCode = "404", description = "Visit or doctor not found")
    })
    @DeleteMapping("/cancel/{id}")
    public void cancelVisit(@PathVariable Long id, @RequestParam String doctorEmail) {
        visitService.cancelVisit(id, doctorEmail);
    }
}
