package com.example.medicalclinic.service;

import com.example.medicalclinic.exception.PatientException;
import com.example.medicalclinic.model.Patient;
import com.example.medicalclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.getPatients();
    }

    public Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientException("Patient with email: " + email + " not found"));
    }

    public Patient addPatient(Patient patient) {
        if (patientRepository.findByEmail(patient.getEmail()).isPresent()) {
            throw new PatientException("Patient with email: " + patient.getEmail() + " already exists");
        }
        return patientRepository.save(patient);
    }

    public void removePatientByEmail(String email) {
        boolean removed = patientRepository.deleteByEmail(email);
        if (!removed) {
            throw new PatientException("Patient with email: " + email + " not found");
        }
    }

    public Patient editPatientByEmail(String email, Patient patient) {
        return patientRepository.updateByEmail(patient, email)
                .orElseThrow(() -> new PatientException("Patient with email: " + email + " not found"));
    }
}