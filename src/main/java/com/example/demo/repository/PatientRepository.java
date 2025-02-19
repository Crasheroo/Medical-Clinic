package com.example.demo.repository;

import com.example.demo.model.Patient;
import com.example.demo.service.PatientService;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PatientRepository {
    private List<Patient> patients;

    public PatientRepository(List<Patient> patients) {
        this.patients = patients;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public Optional<Patient> findByEmail(String email) {
        return patients.stream()
                .filter(patient -> patient.getEmail().equalsIgnoreCase(email))
                .findFirst();
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
        } else {
            patients.add(patient);
            return patient;
        }
    }
}
