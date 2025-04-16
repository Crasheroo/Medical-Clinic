package com.example.medicalclinic.controller;

import com.example.medicalclinic.model.CreateDoctorCommand;
import com.example.medicalclinic.model.dto.DoctorDTO;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.service.DoctorService;
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

@Tag(name = "Doctor operations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorService doctorService;

    @Operation(summary = "Get doctors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctors found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableContentDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @GetMapping
    public PageableContentDTO<DoctorDTO> getDoctors(@ParameterObject Pageable pageable) {
        return doctorService.getAllDoctors(pageable);
    }

    @Operation(summary = "Get doctor by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DoctorDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @GetMapping("/{email}")
    public DoctorDTO getDoctorByEmail(@PathVariable("email") String email) {
        return doctorService.getDoctorByEmail(email);
    }

    @Operation(summary = "Remove doctor by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor removed"),
            @ApiResponse(responseCode = "404", description = "Invalid email supplied")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{email}")
    public void removeDoctor(@PathVariable("email") String email) {
        doctorService.removeDoctorByEmail(email);
    }

    @Operation(summary = "Add doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor added",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DoctorDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Doctor already exists")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public DoctorDTO addDoctor(@RequestBody CreateDoctorCommand command) {
        return doctorService.addDoctor(command);
    }

    @Operation(summary = "Edit doctor details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor edited",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DoctorDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @PutMapping("/{email}")
    public DoctorDTO editDoctor(@PathVariable String email, @RequestBody CreateDoctorCommand command) {
        return doctorService.editDoctorByEmail(email, command);
    }

    @Operation(summary = "Assign doctor to facility")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor and facility found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DoctorDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Doctor or facility not found")
    })
    @PostMapping("/{doctorId}/facilities/{facilityId}")
    public DoctorDTO assignDoctorToFacility(@PathVariable Long doctorId, @PathVariable Long facilityId) {
        return doctorService.assignDoctorToFacility(doctorId, facilityId);
    }

    @Operation(summary = "Remove facility from doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor and facility found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DoctorDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Doctor or facility not found")
    })
    @DeleteMapping("/{doctorId}/facilities/{facilityId}")
    public void removeFacilityFromDoctor(@PathVariable Long doctorId, @PathVariable Long facilityId) {
        doctorService.removeFacilityFromDoctor(doctorId, facilityId);
    }
}