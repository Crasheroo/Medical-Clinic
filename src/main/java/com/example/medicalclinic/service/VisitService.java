package com.example.medicalclinic.service;

import com.example.medicalclinic.exception.DoctorException;
import com.example.medicalclinic.exception.PatientException;
import com.example.medicalclinic.exception.VisitException;
import com.example.medicalclinic.mapper.VisitMapper;
import com.example.medicalclinic.model.VisitHelper;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.model.dto.VisitDTO;
import com.example.medicalclinic.model.dto.VisitFilterDTO;
import com.example.medicalclinic.model.entity.Doctor;
import com.example.medicalclinic.model.entity.Patient;
import com.example.medicalclinic.model.entity.Visit;
import com.example.medicalclinic.repository.DoctorRepository;
import com.example.medicalclinic.repository.PatientRepository;
import com.example.medicalclinic.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final VisitHelper visitHelper;

    @Transactional
    public VisitDTO createVisit(Long doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        visitHelper.validateTimes(startTime, endTime);

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorException("Doctor doesnt exist", HttpStatus.NOT_FOUND));

        List<Visit> conflictingVisits = visitHelper.getConflictingVisits(doctorId, startTime, endTime);

        if (!conflictingVisits.isEmpty()) {
            throw new VisitException("Doctor has a visit at this time", HttpStatus.CONFLICT);
        }

        Visit visit = Visit.builder()
                .doctor(doctor)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        Visit save = visitRepository.save(visit);
        return visitMapper.toDto(save);
    }

    @Transactional
    public VisitDTO bookVisit(Long visitId, Long patientId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitException("Visit doesnt exist", HttpStatus.NOT_FOUND));

        if (visit.hasPatient()) {
            throw new VisitException("Visit is already booked", HttpStatus.CONFLICT);
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientException("Patient doesnt exist", HttpStatus.NOT_FOUND));

        visit.setPatient(patient);
        visitRepository.save(visit);
        return visitMapper.toDto(visit);
    }

    @Transactional
    public void cancelVisit(Long visitId, String doctorEmail) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitException("Visit doesnt exist", HttpStatus.NOT_FOUND));

        if (!visit.getDoctor().getEmail().equals(doctorEmail)) {
            throw new VisitException("Only a doctor can cancel visit", HttpStatus.CONFLICT);
        }

        if (visit.getStartTime().isBefore(LocalDateTime.now())) {
            throw new VisitException("Cannot cancel past visit", HttpStatus.CONFLICT);
        }

        visitRepository.delete(visit);
    }

    public PageableContentDTO<VisitDTO> getVisits(VisitFilterDTO filter, Pageable pageable) {
        return visitHelper.filterVisits(filter, visitRepository, pageable);
    }

    @Transactional
    public Visit reserveVisit(Long id, String patientEmail) {
        Visit visit = visitRepository.findById(id)
                .orElseThrow(() -> new VisitException("Visit doesnt exist", HttpStatus.NOT_FOUND));

        Patient patient = patientRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new PatientException("Patient doesnt exist", HttpStatus.NOT_FOUND));

        visit.setPatient(patient);
        return visitRepository.save(visit);
    }
}