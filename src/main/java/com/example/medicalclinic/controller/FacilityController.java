package com.example.medicalclinic.controller;

import com.example.medicalclinic.model.Facility;
import com.example.medicalclinic.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/facilities")
public class FacilityController {
    private final FacilityService facilityService;

    @GetMapping
    public List<Facility> getFacilities() {
        return facilityService.getAllFacilities();
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
}