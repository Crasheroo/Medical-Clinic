package com.example.medicalclinic.service;

import com.example.medicalclinic.dto.PageableContentDTO;
import com.example.medicalclinic.exception.PatientException;
import com.example.medicalclinic.mapper.PatientMapper;
import com.example.medicalclinic.model.Patient;
import com.example.medicalclinic.dto.PatientDTO;
import com.example.medicalclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PageableContentDTO<PatientDTO> getAllPatients(Pageable pageable) {
        Page<Patient> patientPage = patientRepository.findAll(pageable);
        List<PatientDTO> patientDTOS = patientPage.getContent().stream()
                .map(patientMapper::toDTO)
                .toList();

        return PageableContentDTO.from(patientPage, patientDTOS);
    }

    public PatientDTO getPatientByEmail(String email) {
        return patientMapper.toDTO(findPatientByEmail(email));
    }

    public Patient addPatient(Patient patient) {
        if (patientRepository.findByEmail(patient.getEmail()).isPresent()) {
            throw new PatientException("Patient with email: " + patient.getEmail() + " already exists");
        }
        if (patientRepository.findByIdCardNo(patient.getIdCardNo()).isPresent()) {
            throw new PatientException("Patient with IdCardNo: " + patient.getIdCardNo() + " already exists");

        }
        return patientRepository.save(patient);
    }

    public void removePatientByEmail(String email) {
        patientRepository.delete(findPatientByEmail(email));
    }

    public PatientDTO editPatientByEmail(String email, Patient updatedPatient) {
        Patient existingPatient = findPatientByEmail(email);
        updateEmailIfChanged(existingPatient, updatedPatient);
        existingPatient.updateFrom(updatedPatient);
        return patientMapper.toDTO(patientRepository.save(existingPatient));
    }

    public Patient changePassword(String email, String password) {
        Patient existingPatient = findPatientByEmail(email);
        existingPatient.setPassword(password);
        return patientRepository.save(existingPatient);
    }

    private Patient findPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientException("Patient with email: " + email + " not found"));
    }

    private void updateEmailIfChanged(Patient existingPatient, Patient updatedPatient) {
        String newEmail = updatedPatient.getEmail();
        if (newEmail != null && !newEmail.equals(existingPatient.getEmail())) {
            if (patientRepository.findByEmail(newEmail).isPresent()) {
                throw new PatientException("Email " + newEmail + " is already in use.");
            }
            existingPatient.setEmail(newEmail);
        }
    }
}