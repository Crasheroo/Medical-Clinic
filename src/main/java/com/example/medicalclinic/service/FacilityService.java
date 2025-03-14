package com.example.medicalclinic.service;

import com.example.medicalclinic.dto.CreateDoctorRequest;
import com.example.medicalclinic.dto.FacilityDTO;
import com.example.medicalclinic.dto.PageableContentDTO;
import com.example.medicalclinic.exception.FacilityException;
import com.example.medicalclinic.mapper.FacilityMapper;
import com.example.medicalclinic.model.Doctor;
import com.example.medicalclinic.model.Facility;
import com.example.medicalclinic.dto.CreateFacilityRequest;
import com.example.medicalclinic.repository.DoctorRepository;
import com.example.medicalclinic.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final DoctorRepository doctorRepository;
    private final FacilityMapper facilityMapper;

    public PageableContentDTO<FacilityDTO> getAllFacilities(Pageable pageable) {
        Page<Facility> facilityPage = facilityRepository.findAll(pageable);
        List<FacilityDTO> facilityDTOS = facilityPage.getContent().stream()
                .map(facilityMapper::toDto)
                .toList();

        return PageableContentDTO.from(facilityPage, facilityDTOS);
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
    public FacilityDTO saveFacilityWithDoctors(CreateFacilityRequest request) {
        Facility facility = prepareFacility(request);
        assignDoctorsToFacility(facility, request.getDoctors());
        Facility savedFacility = facilityRepository.save(facility);
        return facilityMapper.toDto(savedFacility);
    }

    @Transactional
    public List<FacilityDTO> saveFacilitiesWithDoctors(List<CreateFacilityRequest> requests) {
        List<Facility> facilities = requests.stream()
                .map(request -> {
                    Facility facility = prepareFacility(request);
                    assignDoctorsToFacility(facility, request.getDoctors());
                    return facility;
                })
                .toList();

        return facilityMapper.listToDto(facilityRepository.saveAll(facilities));
    }

    private Facility prepareFacility(CreateFacilityRequest request) {
        return facilityRepository.findByFacilityName(request.getFacilityName())
                .orElseGet(() -> Facility.from(request));
    }

    private void assignDoctorsToFacility(Facility facility, List<CreateDoctorRequest> doctorRequests) {
        Set<Doctor> doctors = Optional.ofNullable(doctorRequests)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(doctorRequest -> doctorRepository.findByEmail(doctorRequest.getEmail())
                        .orElseGet(() -> Doctor.from(doctorRequest)))
                .collect(Collectors.toSet());

        doctors.forEach(doctor -> doctor.getFacilities().add(facility));
        facility.getDoctors().addAll(doctors);
    }
}