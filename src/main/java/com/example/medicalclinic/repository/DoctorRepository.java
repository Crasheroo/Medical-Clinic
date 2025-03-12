package com.example.medicalclinic.repository;

import com.example.medicalclinic.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmail(String email);
    List<Doctor> findByEmailIn(List<String> emails);
    Page<Doctor> findAll(Pageable pageable);
}
