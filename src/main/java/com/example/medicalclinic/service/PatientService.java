package com.example.medicalclinic.service;

import com.example.medicalclinic.exception.PatientException;
import com.example.medicalclinic.mapper.PatientMapper;
import com.example.medicalclinic.model.Patient;
import com.example.medicalclinic.model.PatientDTO;
import com.example.medicalclinic.model.ResponseMessage;
import com.example.medicalclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public List<PatientDTO> getAllPatients() {
         return patientMapper.toDTOList(patientRepository.getPatients());
    }

    public PatientDTO getPatientByEmail(String email) {
        return patientMapper.toDTO(patientRepository.findByEmail(email).get());
    }

    public Patient addPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public ResponseMessage removePatientByEmail(String email) {
        boolean removed = patientRepository.deleteByEmail(email);
        if (!removed) {
            throw new PatientException("Patient with email: " + email + " not found");
        }
        return new ResponseMessage("Patient with email: " + email + " has been removed.");
    }

    public PatientDTO editPatientByEmail(String email, Patient updatedPatient) {
        Patient updated = patientRepository.updateByEmail(email, updatedPatient);
        return patientMapper.toDTO(updated);
    }

    public Patient changePassword(String email, String password) {
        if (password == null) {
            throw new PatientException("Password cannot be null");
        }
        return patientRepository.updatePasswordByEmail(email, password);
    }
}