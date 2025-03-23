package com.example.medicalclinic.service;

import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.exception.PatientException;
import com.example.medicalclinic.mapper.PatientMapper;
import com.example.medicalclinic.model.entity.Patient;
import com.example.medicalclinic.model.dto.PatientDTO;
import com.example.medicalclinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

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
        return patientMapper.toDTO(patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientException("Patient doesnt exist")));
    }

    public Patient addPatient(Patient patient) {
        patientRepository.findByEmail(patient.getEmail())
                .ifPresent(existing -> { throw new PatientException("Patient with email: " + patient.getEmail() + " already exists"); });

        patientRepository.findByIdCardNo(patient.getIdCardNo())
                .ifPresent(existing -> { throw new PatientException("Patient with IdCardNo: " + patient.getIdCardNo() + " already exists"); });

        return patientRepository.save(patient);
    }

    public void removePatientByEmail(String email) {
        patientRepository.delete(patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientException("Patient doesnt exist")));
    }

    public PatientDTO editPatientByEmail(String email, Patient updatedPatient) {
        Patient existingPatient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientException("Patient doesnt exist"));
        updateEmailIfChanged(existingPatient, updatedPatient);
        existingPatient.updateFrom(updatedPatient);
        return patientMapper.toDTO(patientRepository.save(existingPatient));
    }

    public Patient changePassword(String email, String password) {
        Patient existingPatient = patientRepository.findByEmail(email)
                        .orElseThrow(() -> new PatientException("Patient doesnt exist"));
        existingPatient.setPassword(password);
        return patientRepository.save(existingPatient);
    }


    private void updateEmailIfChanged(Patient existingPatient, Patient updatedPatient) {
        Optional.ofNullable(updatedPatient.getEmail())
                .filter(newEmail -> !newEmail.equals(existingPatient.getEmail()))
                .ifPresent(newEmail -> {
                    if (patientRepository.findByEmail(newEmail).isPresent()) {
                        throw new PatientException("Email " + newEmail + " is already in use.");
                    }
                    existingPatient.setEmail(newEmail);
                });
    }
}