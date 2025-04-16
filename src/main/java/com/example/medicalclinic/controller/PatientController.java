package com.example.medicalclinic.controller;

import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.model.ChangePasswordCommand;
import com.example.medicalclinic.model.entity.Patient;
import com.example.medicalclinic.model.dto.PatientDTO;
import com.example.medicalclinic.service.PatientService;
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

@Tag(name = "Patient operations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/patients")
public class PatientController {
    private final PatientService patientService;

    @Operation(summary = "Get patients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "patients found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableContentDTO.class))}),
            @ApiResponse(responseCode = "404", description = "patients not found")
    })
    @GetMapping
    public PageableContentDTO<PatientDTO> getPatients(@ParameterObject Pageable pageable) {
        return patientService.getAllPatients(pageable);
    }

    @Operation(summary = "Get patient by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @GetMapping("/{email}")
    public PatientDTO getPatientByEmail(@PathVariable("email") String email) {
        return patientService.getPatientByEmail(email);
    }

    @Operation(summary = "Remove patient by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient removed"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{email}")
    public void removePatient(@PathVariable("email") String email) {
        patientService.removePatientByEmail(email);
    }

    @Operation(summary = "Add patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Patient already exists")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PatientDTO addPatient(@RequestBody Patient patient) {
        return patientService.addPatient(patient);
    }

    @Operation(summary = "Edit patient by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient edited",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @PutMapping("/{email}")
    public PatientDTO editPatient(@PathVariable("email") String email, @RequestBody Patient patient) {
        return patientService.editPatientByEmail(email, patient);
    }

    @Operation(summary = "Edit patients password by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patients password edited",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @PatchMapping("/{email}/password")
    public Patient editPatientPassword(@PathVariable String email, @RequestBody ChangePasswordCommand request) {
        return patientService.changePassword(email, request.password());
    }
}
