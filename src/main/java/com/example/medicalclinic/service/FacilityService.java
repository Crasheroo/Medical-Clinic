package com.example.medicalclinic.service;

import com.example.medicalclinic.exception.DoctorException;
import com.example.medicalclinic.exception.FacilityException;
import com.example.medicalclinic.model.Doctor;
import com.example.medicalclinic.model.Facility;
import com.example.medicalclinic.repository.DoctorRepository;
import com.example.medicalclinic.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FacilityService {
    private final FacilityRepository facilityRepository;

    public List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

    public Facility getFacilityByName(String facilityName) {
        return findFacilityByName(facilityName);
    }

    public Facility addFacility(Facility facility) {
        if (facilityRepository.findByFacilityName(facility.getFacilityName()).isPresent()) {
            throw new FacilityException("Facility with name: " + facility.getFacilityName() + " already exists");
        }
        return facilityRepository.save(facility);
    }

    public void removeFacilityByName(String facilityName) {
        Facility facility = findFacilityByName(facilityName);
        facilityRepository.delete(facility);
    }

    public Facility updateByName(String facilityName, Facility updatedFacility) {
        Facility existingFacility = findFacilityByName(facilityName);
        existingFacility.updateFrom(updatedFacility);
        return facilityRepository.save(existingFacility);
    }


    private Facility findFacilityByName(String facilityName) {
        return facilityRepository.findByFacilityName(facilityName)
                .orElseThrow(() -> new FacilityException("Facility with name: " + facilityName + " not found"));
    }
}