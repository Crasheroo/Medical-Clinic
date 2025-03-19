package com.example.medicalclinic.service;

import com.example.medicalclinic.model.EntityFinder;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.exception.DoctorException;
import com.example.medicalclinic.mapper.DoctorMapper;
import com.example.medicalclinic.model.entity.Doctor;
import com.example.medicalclinic.model.dto.DoctorDTO;
import com.example.medicalclinic.model.entity.Facility;
import com.example.medicalclinic.repository.DoctorRepository;
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
    private final DoctorMapper doctorMapper;
    private final EntityFinder entityFinder;

    public PageableContentDTO<DoctorDTO> getAllDoctors(Pageable pageable) {
        Page<Doctor> doctorPage = doctorRepository.findAll(pageable);
        List<DoctorDTO> doctorDTOS = doctorPage.getContent().stream()
                .map(doctorMapper::toDTO)
                .toList();

        return PageableContentDTO.from(doctorPage, doctorDTOS);
    }

    public DoctorDTO getDoctorByEmail(String email) {
        return doctorMapper.toDTO(entityFinder.getDoctorByEmail(email));
    }

    @Transactional
    public DoctorDTO addDoctor(Doctor doctor) {
        doctorRepository.findByEmail(doctor.getEmail())
                .ifPresent(existing -> { throw new DoctorException("Doctor with email: " + doctor.getEmail() + " already exists"); });
        return doctorMapper.toDTO(doctorRepository.save(doctor));
    }

    @Transactional
    public void removeDoctorByEmail(String email) {
        Doctor doctor = entityFinder.getDoctorByEmail(email);
        doctorRepository.delete(doctor);
    }

    public DoctorDTO editDoctorByEmail(String email, Doctor updatedDoctor) {
        Doctor existingDoctor = entityFinder.getDoctorByEmail(email);
        updateEmailIfChanged(existingDoctor, updatedDoctor);
        existingDoctor.updateFrom(updatedDoctor);
        return doctorMapper.toDTO(doctorRepository.save(existingDoctor));
    }

    public Doctor changePassword(String email, String password) {
        Doctor existingDoctor = entityFinder.getDoctorByEmail(email);
        existingDoctor.setPassword(password);
        return doctorRepository.save(existingDoctor);
    }

    @Transactional
    public DoctorDTO assignDoctorToFacility(Long doctorId, Long facilityId) {
        Doctor doctor = entityFinder.getDoctorById(doctorId);
        Facility facility = entityFinder.getFacilityById(facilityId);
        doctor.getFacilities().add(facility);
        return doctorMapper.toDTO(doctorRepository.save(doctor));
    }

    @Transactional
    public void removeFacilityFromDoctor(Long doctorId, Long facilityId) {
        Doctor doctor = entityFinder.getDoctorById(doctorId);
        Facility facility = entityFinder.getFacilityById(facilityId);

        doctor.getFacilities().remove(facility);
        doctorRepository.save(doctor);
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
