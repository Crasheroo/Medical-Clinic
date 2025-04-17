package com.example.medicalclinic.controller;

import com.example.medicalclinic.model.dto.FacilityDTO;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.model.CreateFacilityCommand;
import com.example.medicalclinic.service.FacilityService;
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

import java.util.List;

@Tag(name = "Facility operations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/facilities")
public class FacilityController {
    private final FacilityService facilityService;

    @Operation(summary = "Get facilities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facility found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableContentDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Facility not found")
    })
    @GetMapping
    public PageableContentDTO<FacilityDTO> getFacilities(@ParameterObject Pageable pageable) {
        return facilityService.getAllFacilities(pageable);
    }

    @Operation(summary = "Get facility by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facility found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FacilityDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Facility not found")
    })
    @GetMapping("/{facilityName}")
    public FacilityDTO getFacilityByName(@PathVariable("facilityName") String facilityName) {
        return facilityService.getFacilityByName(facilityName);
    }

    @Operation(summary = "Remove facility by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facility removed"),
            @ApiResponse(responseCode = "404", description = "Facility not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{facilityName}")
    public void removeFacility(@PathVariable("facilityName") String facilityName) {
        facilityService.removeFacilityByName(facilityName);
    }

    @Operation(summary = "Edit facility by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facility found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FacilityDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Facility not found")
    })
    @PutMapping("/{facilityName}")
    public FacilityDTO editFacility(@PathVariable String facilityName, @RequestBody FacilityDTO facility) {
        return facilityService.updateByName(facilityName, facility);
    }

    @Operation(summary = "Create facilities with or without doctors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facility created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FacilityDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Facility already exists")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public List<FacilityDTO> createFacilitiesWithDoctors(@RequestBody List<CreateFacilityCommand> requests) {
        return facilityService.saveFacilitiesWithDoctors(requests);
    }
}