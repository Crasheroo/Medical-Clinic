package com.example.demo.service;

import com.example.demo.model.Patient;
import com.example.demo.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> getAllPatients() {
        return patientRepository.getPatients();
    }

    public Patient getPatientByEmail(String email) {
        List<Patient> allPatients = getAllPatients();

        return allPatients.stream()
                .filter(patient -> patient.getEmail().equalsIgnoreCase(email))
                .findAny()
                .get();
    }

    public Patient addNewPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public void removePatientByEmail(String email) {
        Patient patientByEmail = getPatientByEmail(email);
        if (getAllPatients().contains(patientByEmail)) {
            getAllPatients().remove(patientByEmail);
        }
    }

    public Patient editPatientByEmail(String email, String password, Long idCardNo, String firstName, String lastName, String phoneNumber, String birthday) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Patient with email " + email + " not found"));

        patient.setPassword(password);
        patient.setIdCardNo(idCardNo);
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setPhoneNumber(phoneNumber);
        patient.setBirthday(birthday);

        return patientRepository.save(patient);
    }
}
