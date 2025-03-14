package com.example.medicalclinic.controller;

import com.example.medicalclinic.dto.FacilityDTO;
import com.example.medicalclinic.dto.PageableContentDTO;
import com.example.medicalclinic.mapper.FacilityMapper;
import com.example.medicalclinic.model.Facility;
import com.example.medicalclinic.model.CreateFacilityRequest;
import com.example.medicalclinic.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/facilities")
public class FacilityController {
    private final FacilityService facilityService;
    private final FacilityMapper facilityMapper;

    @GetMapping
    public PageableContentDTO<FacilityDTO> getFacilities(Pageable pageable) {
        return facilityService.getAllFacilities(pageable);
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

    @PutMapping("/{facilityName}")
    public Facility editFacility(@PathVariable String facilityName, @RequestBody FacilityDTO facility) {
        return facilityService.updateByName(facilityName, facilityMapper.toEntity(facility));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public List<FacilityDTO> createFacilitiesWithDoctors(@RequestBody List<CreateFacilityRequest> requests) {
        return facilityService.saveFacilitiesWithDoctors(requests);
    }
}