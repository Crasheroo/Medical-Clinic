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
    private final DoctorRepository doctorRepository;

    public List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

    public Facility getFacilityByName(String facilityName) {
        return facilityRepository.findByFacilityName(facilityName)
                .orElseThrow(() -> new FacilityException("Facility with name: " + facilityName + " not found"));
    }

    public Facility addFacility(Facility facility) {
        if (facilityRepository.findByFacilityName(facility.getFacilityName()).isPresent()) {
            throw new FacilityException("Facility with name: " + facility.getFacilityName() + " already exists");
        }
        return facilityRepository.save(facility);
    }

    public void removeFacilityByName(String facilityName) {
        Facility facility = facilityRepository.findByFacilityName(facilityName)
                .orElseThrow(() -> new FacilityException("Facility with name: " + facilityName + " not found"));
        facilityRepository.delete(facility);
    }

    public Facility updateByName(String facilityName, Facility updatedFacility) {
        Facility existingFacility = facilityRepository.findByFacilityName(facilityName)
                .orElseThrow(() -> new FacilityException("Facility with name: " + facilityName + " not found"));

        existingFacility.updateFrom(updatedFacility);
        return facilityRepository.save(existingFacility);
    }

    public List<String> assignDoctorToFacility(String facilityName, Long doctorId) {
        Facility facility = facilityRepository.findByFacilityName(facilityName)
                .orElseThrow(() -> new FacilityException("Facility with name: " + facilityName + " not found"));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorException("Doctor with ID: " + doctorId + " not found"));

        if (!facility.getDoctors().contains(doctor)) {
            facility.getDoctors().add(doctor);
            doctor.getFacilities().add(facility);
        }

        facilityRepository.save(facility);
        doctorRepository.save(doctor);

        return facility.getDoctors().stream()
                .map(Doctor::getEmail)
                .collect(Collectors.toList());
    }

    public void removeDoctorFromFacility(String facilityName, String email) {
        Facility facility = facilityRepository.findByFacilityName(facilityName)
                .orElseThrow(() -> new FacilityException("Facility with name: " + facilityName + " not found"));

        Doctor doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new DoctorException("Doctor with email: " + email + " not found"));

        if (facility.getDoctors().contains(doctor)) {
            facility.getDoctors().remove(doctor);
            doctor.getFacilities().remove(facility);

            facilityRepository.save(facility);
            doctorRepository.save(doctor);
        }
    }
}