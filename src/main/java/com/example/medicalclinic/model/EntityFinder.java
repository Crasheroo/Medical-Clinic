package com.example.medicalclinic.model;

import com.example.medicalclinic.exception.DoctorException;
import com.example.medicalclinic.exception.FacilityException;
import com.example.medicalclinic.exception.PatientException;
import com.example.medicalclinic.exception.VisitException;
import com.example.medicalclinic.model.entity.Doctor;
import com.example.medicalclinic.model.entity.Facility;
import com.example.medicalclinic.model.entity.Patient;
import com.example.medicalclinic.model.entity.Visit;
import com.example.medicalclinic.repository.DoctorRepository;
import com.example.medicalclinic.repository.FacilityRepository;
import com.example.medicalclinic.repository.PatientRepository;
import com.example.medicalclinic.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityFinder {
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;
    private final FacilityRepository facilityRepository;

    public Patient getPatientById(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientException("Patient doesn't exist"));
    }

    public Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientException("Patient doesn't exist"));
    }

    public Visit getVisitById(Long visitId) {
        return visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitException("Visit doesn't exist"));
    }

    public Doctor getDoctorById(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorException("Doctor doesn't exist"));
    }

    public Doctor getDoctorByEmail(String email) {
        return doctorRepository.findByEmail(email)
                .orElseThrow(() -> new FacilityException("Doctor with email: " + email + " not found"));
    }

    public Facility getFacilityByName(String facilityName) {
        return facilityRepository.findByFacilityName(facilityName)
                .orElseThrow(() -> new FacilityException("Facility with name: " + facilityName + " not found"));
    }

    public Facility getFacilityById(Long facilityId) {
        return facilityRepository.findById(facilityId)
                .orElseThrow(() -> new FacilityException("Facility with ID: " + facilityId + " not found"));
    }
}
