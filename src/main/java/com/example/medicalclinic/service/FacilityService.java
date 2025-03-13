package com.example.medicalclinic.service;

import com.example.medicalclinic.dto.DoctorRequestDTO;
import com.example.medicalclinic.dto.FacilityDTO;
import com.example.medicalclinic.dto.PageableContentDTO;
import com.example.medicalclinic.exception.FacilityException;
import com.example.medicalclinic.mapper.FacilityMapper;
import com.example.medicalclinic.model.Doctor;
import com.example.medicalclinic.model.Facility;
import com.example.medicalclinic.dto.FacilityRequestDTO;
import com.example.medicalclinic.repository.DoctorRepository;
import com.example.medicalclinic.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        return new PageableContentDTO<>(
                facilityPage.getTotalPages(),
                facilityPage.getTotalElements(),
                facilityPage.getNumber(),
                facilityDTOS
        );
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

        return facilityMapper.listToDto(facilityRepository.saveAll(facilities));
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

    private void assignDoctorsToFacility(Facility facility, List<DoctorRequestDTO> doctorRequests) {
        Set<Doctor> doctors = new HashSet<>();

        if (doctorRequests != null && !doctorRequests.isEmpty()) {
            for (DoctorRequestDTO doctorRequest : doctorRequests) {
                Doctor doctor = doctorRepository.findByEmail(doctorRequest.getEmail())
                        .orElseGet(() -> {
                            Doctor newDoctor = Doctor.builder()
                                    .email(doctorRequest.getEmail())
                                    .password(doctorRequest.getPassword())
                                    .facilities(new HashSet<>())
                                    .build();
                            return doctorRepository.save(newDoctor);
                        });
                doctors.add(doctor);
            }
        }
        doctors.forEach(doctor -> doctor.getFacilities().add(facility));
        facility.getDoctors().addAll(doctors);
    }
}