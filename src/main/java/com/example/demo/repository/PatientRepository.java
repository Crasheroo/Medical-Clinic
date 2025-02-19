package com.example.demo.repository;

import com.example.demo.model.Patient;

import java.util.List;

public class PatientRepository {
    private List<Patient> patients;

    public PatientRepository(List<Patient> patients) {
        this.patients = patients;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }
}
