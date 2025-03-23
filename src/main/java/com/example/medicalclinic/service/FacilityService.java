package com.example.medicalclinic.service;

import com.example.medicalclinic.exception.DoctorException;
import com.example.medicalclinic.exception.FacilityException;
import com.example.medicalclinic.model.CreateDoctorCommand;
import com.example.medicalclinic.model.dto.FacilityDTO;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.mapper.FacilityMapper;
import com.example.medicalclinic.model.entity.Doctor;
import com.example.medicalclinic.model.entity.Facility;
import com.example.medicalclinic.model.CreateFacilityCommand;
import com.example.medicalclinic.repository.DoctorRepository;
import com.example.medicalclinic.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
        return facilityRepository.findByFacilityName(facilityName)
                .orElseThrow(() -> new FacilityException("Facility doesnt exist"));
    }

    public void removeFacilityByName(String facilityName) {
        Facility facility = facilityRepository.findByFacilityName(facilityName)
                .orElseThrow(() -> new FacilityException("Facility doesnt exist"));
        facilityRepository.delete(facility);
    }

    public Facility updateByName(String facilityName, Facility updatedFacility) {
        Facility existingFacility = facilityRepository.findByFacilityName(facilityName)
                .orElseThrow(() -> new FacilityException("Facility doesnt exist"));
        existingFacility.updateFrom(updatedFacility);
        return facilityRepository.save(existingFacility);
    }

    @Transactional
    public List<FacilityDTO> saveFacilitiesWithDoctors(List<CreateFacilityCommand> requests) {
        List<Facility> facilities = new ArrayList<>();

        requests.forEach(request -> {
            Facility facility = prepareFacility(request);
            assignDoctorsToFacility(facility, request.doctors());
            facilities.add(facility);
        });

        return facilityMapper.listToDto(facilityRepository.saveAll(facilities));
    }

    private Facility prepareFacility(CreateFacilityCommand request) {
        return facilityRepository.findByFacilityName(request.facilityName())
                .orElseGet(() -> Facility.from(request));
    }

    private void assignDoctorsToFacility(Facility facility, List<CreateDoctorCommand> doctorRequests) {
        Set<Doctor> doctors = new HashSet<>();

        Optional.ofNullable(doctorRequests)
                .orElseGet(Collections::emptyList)
                .forEach(doctorRequest -> {
                    Doctor doctor = doctorRepository.findByEmail(doctorRequest.email())
                            .orElseGet(() -> Doctor.from(doctorRequest));
                    doctors.add(doctor);
                });

        doctors.forEach(doctor -> doctor.getFacilities().add(facility));
        facility.getDoctors().addAll(doctors);
    }
}