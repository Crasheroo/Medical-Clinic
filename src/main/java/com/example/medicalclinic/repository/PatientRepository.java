package com.example.medicalclinic.repository;

import com.example.medicalclinic.exception.PatientException;
import com.example.medicalclinic.model.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@RequiredArgsConstructor
@Repository
public class PatientRepository {
    private final List<Patient> patients;

    public List<Patient> getPatients() {
        return List.copyOf(patients);
    }

    public boolean deleteByEmail(String email) {
        return patients.removeIf(patient -> patient.getEmail().equalsIgnoreCase(email));
    }

    public Optional<Patient> findByEmail(String email) {
        return patients.stream()
                .filter(patient -> patient.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public Patient updatePasswordByEmail(String email, String password) {
        return findByEmail(email)
                .map(patient -> {
                    patient.setPassword(password);
                    return patient;
                })
                .orElseThrow(() -> new PatientException("Patient with email: " + email + " not found"));
    }

    public Patient updateByEmail(Patient updatedPatient, String referencedEmail) {
        Patient existingPatient = findByEmail(referencedEmail)
                .orElseThrow(() -> new PatientException("Patient with email: " + referencedEmail + " not found"));
        existingPatient.updateFrom(updatedPatient);
        return existingPatient;
    }

    public Patient save(Patient patient) {
        if (findByEmail(patient.getEmail()).isPresent()) {
            throw new PatientException("Patient with email: " + patient.getEmail() + " already exists");
        }
        patients.add(patient);
        return patient;
    }
}