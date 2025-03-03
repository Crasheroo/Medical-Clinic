package com.example.medicalclinic.repository;

import com.example.medicalclinic.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientDAO extends JpaRepository<Patient, Long> {
    Patient findByEmail(String email);
}
