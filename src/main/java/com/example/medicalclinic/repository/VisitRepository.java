package com.example.medicalclinic.repository;

import com.example.medicalclinic.model.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByDoctorId(Long doctorId);
}
