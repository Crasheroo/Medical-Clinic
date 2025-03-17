package com.example.medicalclinic.service;

import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.exception.DoctorException;
import com.example.medicalclinic.exception.FacilityException;
import com.example.medicalclinic.model.mapper.DoctorMapper;
import com.example.medicalclinic.model.entity.Doctor;
import com.example.medicalclinic.model.dto.DoctorDTO;
import com.example.medicalclinic.model.entity.Facility;
import com.example.medicalclinic.repository.DoctorRepository;
import com.example.medicalclinic.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final FacilityRepository facilityRepository;
    private final DoctorMapper doctorMapper;

    public PageableContentDTO<DoctorDTO> getAllDoctors(Pageable pageable) {
        Page<Doctor> doctorPage = doctorRepository.findAll(pageable);
        List<DoctorDTO> doctorDTOS = doctorPage.getContent().stream()
                .map(doctorMapper::toDTO)
                .toList();

        return PageableContentDTO.from(doctorPage, doctorDTOS);
    }

    public DoctorDTO getDoctorByEmail(String email) {
        return doctorMapper.toDTO(findDoctorByEmail(email));
    }

    @Transactional
    public DoctorDTO addDoctor(Doctor doctor) {
        if (doctorRepository.findByEmail(doctor.getEmail()).isPresent()) {
            throw new DoctorException("Doctor with email: " + doctor.getEmail() + " already exists");
        }
        return doctorMapper.toDTO(doctorRepository.save(doctor));
    }

    @Transactional
    public void removeDoctorByEmail(String email) {
        Doctor doctor = findDoctorByEmail(email);
        doctorRepository.delete(doctor);
    }

    public DoctorDTO editDoctorByEmail(String email, Doctor updatedDoctor) {
        Doctor existingDoctor = findDoctorByEmail(email);
        updateEmailIfChanged(existingDoctor, updatedDoctor);
        existingDoctor.updateFrom(updatedDoctor);
        return doctorMapper.toDTO(doctorRepository.save(existingDoctor));
    }

    public Doctor changePassword(String email, String password) {
        Doctor existingDoctor = findDoctorByEmail(email);
        existingDoctor.setPassword(password);
        return doctorRepository.save(existingDoctor);
    }

    @Transactional
    public DoctorDTO assignDoctorToFacility(Long doctorId, Long facilityId) {
        Doctor doctor = findDoctorById(doctorId);
        Facility facility = findFacilityById(facilityId);

        if (!doctor.getFacilities().contains(facility)) {
            doctor.getFacilities().add(facility);
        }

        return doctorMapper.toDTO(doctorRepository.save(doctor));
    }

    @Transactional
    public void removeFacilityFromDoctor(Long doctorId, Long facilityId) {
        Doctor doctor = findDoctorById(doctorId);
        Facility facility = findFacilityById(facilityId);

        if (doctor.getFacilities().contains(facility)) {
            doctor.getFacilities().remove(facility);
            doctorRepository.save(doctor);
        }
    }

    private Doctor findDoctorByEmail(String email) {
        return doctorRepository.findByEmail(email)
                .orElseThrow(() -> new DoctorException("Doctor with email: " + email + " not found"));
    }

    private Doctor findDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorException("Doctor with ID: " + id + " not found"));
    }

    private Facility findFacilityById(Long facilityId) {
        return facilityRepository.findById(facilityId)
                .orElseThrow(() -> new FacilityException("Facility with ID: " + facilityId + " not found"));
    }

    private void updateEmailIfChanged(Doctor existingDoctor, Doctor updatedDoctor) {
        String newEmail = updatedDoctor.getEmail();
        if (isEmailChanged(existingDoctor, newEmail)) {
            if (doctorRepository.findByEmail(newEmail).isPresent()) {
                throw new DoctorException("Email " + newEmail + " is already in use.");
            }
            existingDoctor.setEmail(newEmail);
        }
    }

    private boolean isEmailChanged(Doctor doctor, String newEmail) {
        return newEmail != null && !newEmail.equals(doctor.getEmail());
    }
}
