package com.example.medicalclinic.service;

import com.example.medicalclinic.exception.PatientException;
import com.example.medicalclinic.mapper.PatientMapper;
import com.example.medicalclinic.model.Patient;
import com.example.medicalclinic.model.PatientDTO;
import com.example.medicalclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(patientMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PatientDTO getPatientByEmail(String email) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientException("Patient with email: " + email + " not found"));
        return patientMapper.toDTO(patient);
    }

    public Patient addPatient(Patient patient) {
        if (patientRepository.findByEmail(patient.getEmail()).isPresent()) {
            throw new PatientException("Patient with email: " + patient.getEmail() + " already exists");
        }
        return patientRepository.save(patient);
    }

    public void removePatientByEmail(String email) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientException("Patient with email: " + email + " not found"));
        patientRepository.delete(patient);
    }

    public PatientDTO editPatientByEmail(String email, Patient updatedPatient) {
        Patient updated = updateByEmail(email, updatedPatient);
        return patientMapper.toDTO(updated);
    }

    public Patient updateByEmail(String email, Patient updatedPatient) {
        Patient existingPatient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientException("Patient with email: " + email + " not found"));

        if (updatedPatient.getEmail() != null && !updatedPatient.getEmail().equals(email)) {
            if (patientRepository.findByEmail(updatedPatient.getEmail()).isPresent()) {
                throw new PatientException("Email " + updatedPatient.getEmail() + " is already in use.");
            }
            existingPatient.setEmail(updatedPatient.getEmail());
        }

        existingPatient.updateFrom(updatedPatient);
        return patientRepository.save(existingPatient);
    }

    public Patient changePassword(String email, String password) {
        Patient existingPatient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientException("Patient with email: " + email + " not found"));

        existingPatient.setPassword(password);
        return patientRepository.save(existingPatient);
    }
}