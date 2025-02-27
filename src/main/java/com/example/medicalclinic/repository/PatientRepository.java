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

    public Optional<Patient> findByIdCardNo(String idCardNo) {
        return patients.stream()
                .filter(patient -> patient.getIdCardNo().equalsIgnoreCase(idCardNo))
                .findFirst();
    }

    public Optional<Patient> findByEmail(String email) {
        return patients.stream()
                .filter(patient -> patient.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public Patient updatePasswordByEmail(String email, String password) {
        Patient existingPatient = findByEmail(email)
                .orElseThrow(() -> new PatientException("Patient with email: " + email + " not found"));

        existingPatient.setPassword(password);
        return existingPatient;
    }

    public Patient updateByEmail(String referencedEmail, Patient updatedPatient) {
        Patient existingPatient = findByEmail(referencedEmail)
                .orElseThrow(() -> new PatientException("Patient with email: " + referencedEmail + " not found"));

        if (updatedPatient.getEmail() != null && !updatedPatient.getEmail().equals(existingPatient.getEmail())) {
            if (findByEmail(updatedPatient.getEmail()).isPresent()) {
                throw new PatientException("E-mail " + updatedPatient.getEmail() + " is already used.");
            }
            existingPatient.setEmail(updatedPatient.getEmail());
        }

        existingPatient.updateFrom(updatedPatient);
        return existingPatient;
    }

    public Patient save(Patient patient) {
        validateNotNullFields(patient);

        if (findByEmail(patient.getEmail()).isPresent()) {
            throw new PatientException("Patient with email: " + patient.getEmail() + " already exists");
        }
        if (findByIdCardNo(patient.getIdCardNo()).isPresent()) {
            throw new PatientException("Patient with ID Card Number: " + patient.getIdCardNo() + " already exists");
        }

        patients.add(patient);
        return patient;
    }

    public void validateNotNullFields(Patient patient) {
        if (patient.getFirstName() == null) {
            throw new PatientException("First name cannot be null");
        }
        if (patient.getLastName() == null) {
            throw new PatientException("Last name cannot be null");
        }
        if (patient.getEmail() == null) {
            throw new PatientException("Email cannot be null");
        }
        if (patient.getPhoneNumber() == null) {
            throw new PatientException("Phone number cannot be null");
        }
        if (patient.getBirthday() == null) {
            throw new PatientException("Birthday cannot be null");
        }
        if (patient.getPassword() == null) {
            throw new PatientException("Password cannot be null");
        }
        if (patient.getIdCardNo() == null) {
            throw new PatientException("ID Card Number cannot be null");
        }
    }
}