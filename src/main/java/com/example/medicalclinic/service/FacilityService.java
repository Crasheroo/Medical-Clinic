package com.example.medicalclinic.service;

import com.example.medicalclinic.dto.DoctorDTO;
import com.example.medicalclinic.dto.FacilityDTO;
import com.example.medicalclinic.exception.FacilityException;
import com.example.medicalclinic.mapper.FacilityMapper;
import com.example.medicalclinic.model.Doctor;
import com.example.medicalclinic.model.Facility;
import com.example.medicalclinic.dto.FacilityRequestDTO;
import com.example.medicalclinic.repository.DoctorRepository;
import com.example.medicalclinic.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final DoctorRepository doctorRepository;
    private final FacilityMapper facilityMapper;

    public List<FacilityDTO> getAllFacilities() {
        return facilityMapper.listToDto(facilityRepository.findAll());
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

    @Transactional
    public FacilityDTO saveFacilityWithDoctors(FacilityRequestDTO request) {
        Facility facility = prepareFacility(request);
        assignDoctorsToFacility(facility, request.getDoctors());
        Facility savedFacility = facilityRepository.save(facility);
        return facilityMapper.toDto(savedFacility);
    }

    @Transactional
    public List<FacilityDTO> saveFacilitiesWithDoctors(List<FacilityRequestDTO> requests) {
        List<Facility> facilities = requests.stream()
                .map(request -> {
                    Facility facility = prepareFacility(request);
                    assignDoctorsToFacility(facility, request.getDoctors());
                    return facility;
                })
                .toList();

        return facilityRepository.saveAll(facilities).stream()
                .map(facilityMapper::toDto)
                .toList();
    }

    private Facility prepareFacility(FacilityRequestDTO request) {
        return facilityRepository.findByFacilityName(request.getFacilityName())
                .orElseGet(() -> Facility.builder()
                        .facilityName(request.getFacilityName())
                        .city(request.getCity())
                        .postcode(request.getPostcode())
                        .street(request.getStreet())
                        .buildingNumber(request.getBuildingNumber())
                        .doctors(new HashSet<>())
                        .build());
    }

    private void assignDoctorsToFacility(Facility facility, Set<DoctorDTO> doctorDTOs) {
        Set<Doctor> doctors = doctorDTOs.stream()
                .map(doctorDTO -> doctorRepository.findByEmail(doctorDTO.getEmail())
                        .orElseGet(() -> doctorRepository.save(
                                Doctor.builder()
                                        .email(doctorDTO.getEmail())
                                        .facilities(new HashSet<>())
                                        .build()
                        )))
                .collect(Collectors.toSet());

        doctors.forEach(doctor -> doctor.getFacilities().add(facility));
        facility.getDoctors().addAll(doctors);
    }
}