package com.example.medicalclinic.service;

import com.example.medicalclinic.exception.PatientException;
import com.example.medicalclinic.mapper.PatientMapper;
import com.example.medicalclinic.model.Patient;
import com.example.medicalclinic.model.PatientDTO;
import com.example.medicalclinic.repository.PatientDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PatientService {
    private final PatientDAO patientDAO;
    private final PatientMapper patientMapper;

    public List<PatientDTO> getAllPatients() {
         return patientDAO.findAll()
                 .stream()
                 .map(patientMapper::toDTO)
                 .collect(Collectors.toList());
    }

    public PatientDTO getPatientByEmail(String email) {
        Patient patient = patientDAO.findByEmail(email);
        if (patient == null) {
            throw new PatientException("Patient with email: " + email + " not found");
        }
        return patientMapper.toDTO(patient);
    }

    public Patient addPatient(Patient patient) {
        if (patientDAO.findByEmail(patient.getEmail()) != null) {
            throw new PatientException("Patient with email: " + patient.getEmail() + " already exists");
        }
        return patientDAO.save(patient);
    }

    public void removePatientByEmail(String email) {
        Patient patient = patientDAO.findByEmail(email);
        if (patient == null) {
            throw new PatientException("Patient with email: " + email + " not found");
        }
        patientDAO.delete(patient);
    }

    public PatientDTO editPatientByEmail(String email, Patient updatedPatient) {
        Patient updated = updateByEmail(email, updatedPatient);
        return patientMapper.toDTO(updated);
    }

    public Patient updateByEmail(String email, Patient updatedPatient) {
        Patient existingPatient = patientDAO.findByEmail(email);
        if (existingPatient == null) {
            throw new PatientException("Patient with email: " + email + " not found");
        }

        if (updatedPatient.getEmail() != null && !updatedPatient.getEmail().equals(email)) {
            Patient patientWithNewEmail = patientDAO.findByEmail(updatedPatient.getEmail());
            if (patientWithNewEmail != null) {
                throw new PatientException("Email " + updatedPatient.getEmail() + " is already in use.");
            }
            existingPatient.setEmail(updatedPatient.getEmail());
        }

        existingPatient.updateFrom(updatedPatient);
        return patientDAO.save(existingPatient);
    }

    public Patient changePassword(String email, String password) {
        Patient existingPatient = patientDAO.findByEmail(email);
        if (existingPatient == null) {
            throw new PatientException("Patient with email: " + email + " not found");
        }

        existingPatient.setPassword(password);
        return patientDAO.save(existingPatient);
    }
}