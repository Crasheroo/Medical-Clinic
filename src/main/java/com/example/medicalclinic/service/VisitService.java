package com.example.medicalclinic.service;

import com.example.medicalclinic.exception.DoctorException;
import com.example.medicalclinic.exception.PatientException;
import com.example.medicalclinic.exception.VisitException;
import com.example.medicalclinic.mapper.VisitMapper;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.model.dto.VisitDTO;
import com.example.medicalclinic.model.entity.Doctor;
import com.example.medicalclinic.model.entity.Patient;
import com.example.medicalclinic.model.entity.Visit;
import com.example.medicalclinic.repository.DoctorRepository;
import com.example.medicalclinic.repository.PatientRepository;
import com.example.medicalclinic.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public VisitDTO createVisit(Long doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        validateTimes(startTime, endTime);

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorException("Doctor doesnt exist"));

        List<Visit> conflictingVisits = getConflictingVisits(doctorId, startTime, endTime);
        if (!conflictingVisits.isEmpty()) {
            throw new VisitException("Doctor has a visit at this time");
        }

        Visit visit = Visit.builder()
                .doctor(doctor)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        visitRepository.save(visit);
        return visitMapper.toDto(visit);
    }

    @Transactional
    public VisitDTO bookVisit(Long visitId, Long patientId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitException("Visit doesnt exist"));

        if (visit.hasPatient()) {
            throw new VisitException("Visit is already booked");
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientException("Patient doesnt exist"));;

        visit.setPatient(patient);
        visitRepository.save(visit);
        return visitMapper.toDto(visit);
    }

    @Transactional
    public PageableContentDTO<VisitDTO> getVisits(Pageable pageable) {
        Page<Visit> visitPage = visitRepository.findAll(pageable);
        List<VisitDTO> visits = visitPage.getContent().stream()
                .map(visitMapper::toDto)
                .toList();

        return PageableContentDTO.from(visitPage, visits);
    }

    @Transactional
    public Visit reserveVisit(Long visitId, String patientEmail) {
        Visit visit = visitRepository.findByIdAndPatientIsNull(visitId)
                .orElseThrow(() -> new VisitException("Visit doesnt exist"));

        Patient patient = patientRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new PatientException("Patient doesnt exist"));

        visit.setPatient(patient);
        return visitRepository.save(visit);
    }

    public List<VisitDTO> getVisitsByPatient(String patientEmail) {
        return visitMapper.toDto(visitRepository.findAllByPatientEmail(patientEmail));
    }

    public List<VisitDTO> getAvailableVisitsByDoctor(Long doctorId) {
        List<Visit> visits = visitRepository.findByDoctorId(doctorId);
        return visitMapper.toDto(visits);
    }

    public List<VisitDTO> getAvailableVisitsBySpecialtyAndDay(String speciality, LocalDateTime date) {
        LocalDateTime startOfDay = date.plusDays(1);
        LocalDateTime endOfDay = date.plusDays(2);
        List<Visit> visits = visitRepository.findByDoctorSpecialtyAndStartTimeBetweenAndPatientIsNull(speciality, startOfDay, endOfDay);
        return visitMapper.toDto(visits);
    }

    private void validateTimes(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new VisitException("Can't create visits in the past");
        }
        if (endTime.isBefore(startTime)) {
            throw new VisitException("End time must be after start time");
        }
        if (startTime.getMinute() % 15 != 0 || endTime.getMinute() % 15 != 0) {
            throw new VisitException("Visits must be in quarter (00, 15, 30, 45)");
        }
    }

    private List<Visit> getConflictingVisits(Long doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        return visitRepository.findByDoctorId(doctorId).stream()
                .filter(visit -> visit.getStartTime().isBefore(endTime) && visit.getEndTime().isAfter(startTime))
                .toList();
    }
}