package com.example.demo.repository;

import com.example.demo.model.Patient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PatientRepository {
    private final List<Patient> patients;

    public PatientRepository(List<Patient> patients) {
        this.patients = patients;
    }

    public List<Patient> getPatients() {
        return new ArrayList<>(patients);
    }

    public Optional<String> deleteByEmail(String email) {
        boolean removed = patients.removeIf(patient -> patient.getEmail().equalsIgnoreCase(email));
        return removed ? Optional.of(email) : Optional.empty();
    }

    public Optional<Patient> findByEmail(String email) {
        return patients.stream()
                .filter(patient -> patient.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public Optional<Patient> updateByEmail(Patient updatedPatient, String referencedEmail) {
        return findByEmail(referencedEmail)
                .map(existingPatient -> {
                existingPatient.setPassword(Optional.ofNullable(updatedPatient.getPassword()).orElse(existingPatient.getPassword()));
                existingPatient.setFirstName(Optional.ofNullable(updatedPatient.getFirstName()).orElse(existingPatient.getFirstName()));
                existingPatient.setLastName(Optional.ofNullable(updatedPatient.getLastName()).orElse(existingPatient.getLastName()));
                existingPatient.setBirthday(Optional.ofNullable(updatedPatient.getBirthday()).orElse(existingPatient.getBirthday()));
                existingPatient.setIdCardNo(Optional.ofNullable(updatedPatient.getIdCardNo()).orElse(existingPatient.getIdCardNo()));
                existingPatient.setEmail(Optional.ofNullable(updatedPatient.getEmail()).orElse(existingPatient.getEmail()));
                existingPatient.setPhoneNumber(Optional.ofNullable(updatedPatient.getPhoneNumber()).orElse(existingPatient.getPhoneNumber()));
                return existingPatient;
                });
    }

    public Patient save(Patient patient) {
        Optional<Patient> existingPatient = findByEmail(patient.getEmail());
        if (existingPatient.isPresent()) {
            Patient existing = existingPatient.get();
            existing.setFirstName(patient.getFirstName());
            existing.setLastName(patient.getLastName());
            existing.setPassword(patient.getPassword());
            existing.setIdCardNo(patient.getIdCardNo());
            existing.setPhoneNumber(patient.getPhoneNumber());
            existing.setBirthday(patient.getBirthday());
            return existing;
        }
        patients.add(patient);
        return patient;
    }
}
