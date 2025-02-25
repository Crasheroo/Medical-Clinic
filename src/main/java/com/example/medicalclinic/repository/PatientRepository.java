package com.example.medicalclinic.repository;

import com.example.medicalclinic.model.Patient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

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

    public Optional<Patient> updateByEmail(Patient updatedPatient, String referencedEmail) {
        return findByEmail(referencedEmail)
                .map(existingPatient -> {
                    existingPatient.updateFrom(updatedPatient);
                    return existingPatient;
                });
    }

    public Patient save(Patient patient) {
        return findByEmail(patient.getEmail())
                .map(existingPatient -> {
                    existingPatient.updateFrom(patient);
                    return existingPatient;
                }).orElseGet(() -> {
                    patients.add(patient);
                    return patient;
                });
    }
}