package com.example.medicalclinic.repository;

import com.example.medicalclinic.model.entity.Visit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByDoctorId(Long doctorId);
    List<Visit> findAllByPatientEmail(String patientEmail);
    List<Visit> findByDoctorSpecialtyAndStartTimeBetweenAndPatientIsNull(String specialty, LocalDateTime startOfDay, LocalDateTime endOfDay);
    Page<Visit> findAllByPatientEmail(String patientEmail, Pageable pageable);
    Page<Visit> findByDoctorIdAndPatientIsNull(Long doctorId, Pageable pageable);
    Page<Visit> findByDoctorSpecialtyAndStartTimeBetweenAndPatientIsNull(
            String specialty, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Visit> findByDoctorId(Long doctorId, Pageable pageable);
}
