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
        validateNotNullFields(patient);
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
        return patientRepository.updateByEmail(patient, email);
    }

    public Patient changePassword(String email, String password) {
        if (password == null) {
            throw new PatientException("Password cannot be null");
        }
        return patientRepository.updatePasswordByEmail(email, password);
    }

    private void validateNotNullFields(Patient patient) {
        if (patient.getFirstName() == null ||
                patient.getLastName() == null ||
                patient.getEmail() == null ||
                patient.getPhoneNumber() == null ||
                patient.getBirthday() == null ||
                patient.getPassword() == null ||
                patient.getIdCardNo() == null) {
            throw new PatientException("Patient fields cannot be null");
        }
    }
}