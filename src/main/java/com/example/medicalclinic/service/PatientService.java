package com.example.medicalclinic.service;

import com.example.medicalclinic.exception.PatientException;
import com.example.medicalclinic.model.Patient;
import com.example.medicalclinic.model.PatientDTO;
import com.example.medicalclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public List<PatientDTO> getAllPatients() {
        return patientRepository.getPatientsAsDTO();
    }

    public PatientDTO getPatientByEmail(String email) {
        return patientRepository.getPatientDTOByEmail(email);
    }

    public Patient addPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public void removePatientByEmail(String email) {
        boolean removed = patientRepository.deleteByEmail(email);
        if (!removed) {
            throw new PatientException("Patient with email: " + email + " not found");
        }
    }

    public Patient editPatientByEmail(String email, Patient updatedPatient) {
        return patientRepository.updateByEmail(email, updatedPatient);
    }

    public Patient changePassword(String email, String password) {
        if (password == null) {
            throw new PatientException("Password cannot be null");
        }
        return patientRepository.updatePasswordByEmail(email, password);
    }
}