package com.example.demo.service;

import com.example.demo.model.Patient;
import com.example.demo.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> getAllPatients() {
        return patientRepository.getPatients();
    }

    public Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Patient with email " + email + " not found"));
    }

    public Patient addPatient(Patient patient) {
        if (patientRepository.findByEmail(patient.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Patient with email " + patient.getEmail() + " already exists");
        }
        return patientRepository.save(patient);
    }

    public void removePatientByEmail(String email) {
        boolean removed = patientRepository.deleteByEmail(email);
        if (!removed) {
            throw new IllegalArgumentException("Patient with email " + email + " not found");
        }
    }

    public Patient editPatientByEmail(String email, Patient patient) {
        return patientRepository.updateByEmail(patient, email)
                .orElseThrow(() -> new IllegalArgumentException("Patient with email " + email + " not found"));
    }
}
