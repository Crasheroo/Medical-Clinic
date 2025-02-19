package com.example.demo.service;

import com.example.demo.model.Patient;
import com.example.demo.repository.PatientRepository;

import java.util.List;
import java.util.Optional;

public class PatientService {
    PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> getAllPatients() {
        return patientRepository.getPatients();
    }

    public Optional<Patient> getPatientByEmail(String email) {
        List<Patient> allPatients = getAllPatients();

        return allPatients.stream()
                .filter(patient -> patient.getEmail().equalsIgnoreCase(email))
                .findAny();
    }

    public void addNewPatient(String email, String password, Long idCardNo, String firstName, String lastName, String phoneNumber, String birthday) {
        if (getPatientByEmail(email).isPresent()) {
            return;
        }

        Patient patient = new Patient(email, password, idCardNo, firstName, lastName, phoneNumber, birthday);
        List<Patient> patients = getAllPatients();
        patients.add(patient);
    }

    public void removePatientByEmail(String email) {
        Optional<Patient> patientByEmail = getPatientByEmail(email);
        patientByEmail.ifPresent(getAllPatients()::remove);
    }

    public void editPatientByEmail(String email, String password, Long idCardNo, String firstName, String lastName, String phoneNumber, String birthday) {
        Optional<Patient> patientByEmail = getPatientByEmail(email);
        patientByEmail.ifPresent(patient -> {
            patient.setPassword(password);
            patient.setIdCardNo(idCardNo);
            patient.setFirstName(firstName);
            patient.setLastName(lastName);
            patient.setPhoneNumber(phoneNumber);
            patient.setBirthday(birthday);
        });

    }
}
