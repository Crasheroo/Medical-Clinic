package com.example.demo.controller;

import com.example.demo.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientController {
    private List<Patient> patients;

    public PatientController(List<Patient> patients) {
        this.patients = new ArrayList<>();
    }

    public List<Patient> getAllPatients() {
        return patients;
    }

    public Optional<Patient> getPatientByEmail(String email) {
        return patients.stream()
                .filter(patient -> patient.getEmail().equalsIgnoreCase(email))
                .findAny();
    }

    public void addNewPatient(String email, String password, Long idCardNo, String firstName, String lastName, String phoneNumber, String birthday) {
        if (getPatientByEmail(email).isPresent()) {
            return;
        }

        Patient patient = new Patient(email, password, idCardNo, firstName, lastName, phoneNumber, birthday);
        patients.add(patient);
    }

    public void removePatientByEmail(String email) {
        Optional<Patient> patientByEmail = getPatientByEmail(email);
        patientByEmail.ifPresent(patients::remove);
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
