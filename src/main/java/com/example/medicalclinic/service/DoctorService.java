package com.example.medicalclinic.service;

import com.example.medicalclinic.exception.DoctorException;
import com.example.medicalclinic.exception.FacilityException;
import com.example.medicalclinic.mapper.DoctorMapper;
import com.example.medicalclinic.model.Doctor;
import com.example.medicalclinic.model.DoctorDTO;
import com.example.medicalclinic.model.Facility;
import com.example.medicalclinic.repository.DoctorRepository;
import com.example.medicalclinic.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final FacilityRepository facilityRepository;
    private final DoctorMapper doctorMapper;

    public List<DoctorDTO> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        doctors.forEach(doctor -> doctor.getFacilities().size());
        return doctors.stream().map(doctorMapper::toDTO).toList();
    }


    public DoctorDTO getDoctorByEmail(String email) {
        Doctor doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new DoctorException("Doctor with email: " + email + " not found"));
        return doctorMapper.toDTO(doctor);
    }

    public Doctor addDoctor(Doctor doctor) {
        if (doctorRepository.findByEmail(doctor.getEmail()).isPresent()) {
            throw new DoctorException("Doctor with email: " + doctor.getEmail() + " already exists");
        }
        return doctorRepository.save(doctor);
    }

    public void removeDoctorByEmail(String email) {
        Doctor doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new DoctorException("Doctor with email: " + email + " not found"));
        doctorRepository.delete(doctor);
    }

    public DoctorDTO editDoctorByEmail(String email, Doctor updatedDoctor) {
        Doctor updated = updateByEmail(email, updatedDoctor);
        return doctorMapper.toDTO(updated);
    }

    public Doctor updateByEmail(String email, Doctor updatedDoctor) {
        Doctor existingDoctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new DoctorException("Doctor with email: " + email + " not found"));

        if (updatedDoctor.getEmail() != null && !updatedDoctor.getEmail().equals(email)) {
            if (doctorRepository.findByEmail(updatedDoctor.getEmail()).isPresent()) {
                throw new DoctorException("Email " + updatedDoctor.getEmail() + " is already in use.");
            }
            existingDoctor.setEmail(updatedDoctor.getEmail());
        }

        existingDoctor.updateFrom(updatedDoctor);
        return doctorRepository.save(existingDoctor);
    }

    public Doctor changePassword(String email, String password) {
        Doctor existingDoctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new DoctorException("Doctor with email: " + email + " not found"));

        existingDoctor.setPassword(password);
        return doctorRepository.save(existingDoctor);
    }

    public DoctorDTO assignDoctorToFacility(Long doctorId, String facilityName) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorException("Doctor with ID: " + doctorId + " not found"));

        Facility facility = facilityRepository.findByFacilityName(facilityName)
                .orElseThrow(() -> new FacilityException("Facility with name: " + facilityName + " not found"));

        if (!doctor.getFacilities().contains(facility)) {
            doctor.getFacilities().add(facility);
            facility.getDoctors().add(doctor);
        }

        doctorRepository.save(doctor);
        facilityRepository.save(facility);

        return doctorMapper.toDTO(doctor);
    }
}
