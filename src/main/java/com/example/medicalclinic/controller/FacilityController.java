package com.example.medicalclinic.controller;

import com.example.medicalclinic.dto.FacilityDTO;
import com.example.medicalclinic.model.Facility;
import com.example.medicalclinic.dto.FacilityRequestDTO;
import com.example.medicalclinic.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/facilities")
public class FacilityController {
    private final FacilityService facilityService;

    @GetMapping
    public List<FacilityDTO> getFacilities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return facilityService.getAllFacilities(PageRequest.of(page, size));
    }

    @GetMapping("/{facilityName}")
    public Facility getFacilityByName(@PathVariable("facilityName") String facilityName) {
        return facilityService.getFacilityByName(facilityName);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{facilityName}")
    public void removeFacility(@PathVariable("facilityName") String facilityName) {
        facilityService.removeFacilityByName(facilityName);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Facility addFacility(@RequestBody Facility facility) {
        return facilityService.addFacility(facility);
    }

    @PutMapping("/{facilityName}")
    public Facility editFacility(@PathVariable String facilityName, @RequestBody Facility facility) {
        return facilityService.updateByName(facilityName, facility);
    }

    @PostMapping("/with-doctors")
    public FacilityDTO createFacilityWithDoctors(@RequestBody FacilityRequestDTO request) {
        return facilityService.saveFacilityWithDoctors(request);
    }

    @PostMapping("/bulk")
    public List<FacilityDTO> createFacilitiesWithDoctors(@RequestBody List<FacilityRequestDTO> requests) {
        return facilityService.saveFacilitiesWithDoctors(requests);
    }
}