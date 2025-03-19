package com.example.medicalclinic.service;

import com.example.medicalclinic.exception.VisitException;
import com.example.medicalclinic.mapper.VisitMapper;
import com.example.medicalclinic.model.EntityFinder;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.model.dto.VisitDTO;
import com.example.medicalclinic.model.entity.Doctor;
import com.example.medicalclinic.model.entity.Patient;
import com.example.medicalclinic.model.entity.Visit;
import com.example.medicalclinic.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final EntityFinder entityFinder;

    @Transactional
    public void createVisit(Long doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        validateTimes(startTime, endTime);

        Doctor doctor = entityFinder.getDoctorById(doctorId);

        Optional.of(isDoctorAvailable(doctorId, startTime, endTime))
                .filter(isNotAvailable -> isNotAvailable)
                .ifPresent(unused -> { throw new VisitException("Doctor has a visit at this time"); });

        Visit visit = Visit.builder()
                .doctor(doctor)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        visitRepository.save(visit);
    }

    @Transactional
    public void bookVisit(Long visitId, Long patientId) {
        Visit visit = entityFinder.getVisitById(visitId);

        Optional.ofNullable(visit.getPatient())
                .ifPresent(patient -> { throw new VisitException("Visit is already booked"); });

        Patient patient = entityFinder.getPatientById(patientId);

        visit.setPatient(patient);
        visitRepository.save(visit);
    }

    @Transactional
    public PageableContentDTO<VisitDTO> getVisits(Pageable pageable) {
        Page<Visit> visitPage = visitRepository.findAll(pageable);
        List<VisitDTO> visits = visitPage.getContent().stream()
                .map(visitMapper::toDto)
                .toList();

        return PageableContentDTO.from(visitPage, visits);
    }

    private void validateTimes(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new VisitException("Can't create visits in the past");
        }
        if (endTime.isBefore(startTime)) {
            throw new VisitException("End time must be after start time");
        }
        if (startTime.getMinute() % 15 != 0 || endTime.getMinute() % 15 != 0) {
            throw new VisitException("Visits must be in kwadrans (00, 15, 30, 45)");
        }
    }

    private boolean isDoctorAvailable(Long doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        return visitRepository.findByDoctorId(doctorId).stream()
                .anyMatch(visit -> visit.getStartTime().isBefore(endTime) && visit.getEndTime().isAfter(startTime));
    }
}
