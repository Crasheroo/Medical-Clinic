package com.example.medicalclinic.service;

import com.example.medicalclinic.exception.DoctorException;
import com.example.medicalclinic.exception.VisitException;
import com.example.medicalclinic.mapper.VisitMapper;
import com.example.medicalclinic.model.VisitHelper;
import com.example.medicalclinic.model.dto.VisitDTO;
import com.example.medicalclinic.model.entity.Doctor;
import com.example.medicalclinic.model.entity.Patient;
import com.example.medicalclinic.model.entity.Visit;
import com.example.medicalclinic.repository.DoctorRepository;
import com.example.medicalclinic.repository.PatientRepository;
import com.example.medicalclinic.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VisitServiceTest {
    private VisitRepository visitRepository;
    private VisitMapper visitMapper;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private VisitService visitService;
    private VisitHelper visitHelper;

    @BeforeEach
    void setUp() {
        this.visitRepository = Mockito.mock(VisitRepository.class);
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.patientRepository = Mockito.mock(PatientRepository.class);
        this.visitMapper = Mappers.getMapper(VisitMapper.class);
        this.visitHelper = Mockito.mock(VisitHelper.class);
        this.visitService = new VisitService(visitRepository, visitMapper, doctorRepository, patientRepository, visitHelper);
    }

    @Test
    void createVisit_shouldCreateVisitWhenDataIsValid() {
        // Given
        Long doctorId = 1L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.withMinute(0).withSecond(0).withNano(0).plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(visitHelper.getConflictingVisits(doctorId, startTime, endTime)).thenReturn(Collections.emptyList());
        when(visitRepository.save(any(Visit.class))).thenAnswer(invocation -> {
            Visit savedVisit = invocation.getArgument(0);
            savedVisit.setId(1L);
            return savedVisit;
        });

        // When
        VisitDTO result = visitService.createVisit(doctorId, startTime, endTime);

        // Then
        assertNotNull(result);
        assertEquals(doctorId, result.getId());
        verify(visitHelper).validateTimes(startTime, endTime);
        verify(visitRepository).save(any(Visit.class));
    }

    @Test
    void createVisit_shouldThrowWhenDoctorNotFound() {
        // Given
        Long doctorId = 1L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(DoctorException.class, () ->
                visitService.createVisit(doctorId, LocalDateTime.now(), LocalDateTime.now().plusHours(1)));
    }

    @Test
    void createVisit_shouldThrowWhenTimeConflictExists() {
        // Given
        Long doctorId = 1L;
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(visitHelper.getConflictingVisits(doctorId, startTime, endTime))
                .thenReturn(List.of(new Visit()));

        // When & Then
        assertThrows(VisitException.class, () ->
                visitService.createVisit(doctorId, startTime, endTime));
    }

    @Test
    void bookVisit_shouldBookVisitWhenDataIsValid() {
        // Given
        Long visitId = 1L;
        Long patientId = 1L;
        Visit visit = new Visit();
        visit.setId(visitId);
        Patient patient = new Patient();
        patient.setId(patientId);

        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(visitRepository.save(any(Visit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        VisitDTO result = visitService.bookVisit(visitId, patientId);

        // Then
        assertNotNull(result);
        assertEquals(patientId, result.getId());
        verify(visitRepository).save(visit);
    }

    @Test
    void bookVisit_shouldThrowWhenVisitNotFound() {
        // Given
        Long visitId = 1L;
        when(visitRepository.findById(visitId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(VisitException.class, () ->
                visitService.bookVisit(visitId, 1L));
    }

    @Test
    void bookVisit_shouldThrowWhenVisitAlreadyBooked() {
        // Given
        Long visitId = 1L;
        Visit visit = new Visit();
        visit.setId(visitId);
        visit.setPatient(new Patient()); // already booked

        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));

        // When & Then
        assertThrows(VisitException.class, () ->
                visitService.bookVisit(visitId, 1L));
    }

    @Test
    void cancelVisit_shouldCancelVisitWhenConditionsMet() {
        // Given
        Long visitId = 1L;
        String doctorEmail = "doctor@example.com";
        Visit visit = new Visit();
        Doctor doctor = new Doctor();
        doctor.setEmail(doctorEmail);
        visit.setDoctor(doctor);
        visit.setStartTime(LocalDateTime.now().plusHours(1));

        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));

        // When
        visitService.cancelVisit(visitId, doctorEmail);

        // Then
        verify(visitRepository).delete(visit);
    }

    @Test
    void cancelVisit_shouldThrowWhenVisitNotFound() {
        // Given
        Long visitId = 1L;
        when(visitRepository.findById(visitId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(VisitException.class, () ->
                visitService.cancelVisit(visitId, "doctor@example.com"));
    }

    @Test
    void cancelVisit_shouldThrowWhenNotDoctorVisit() {
        // Given
        Long visitId = 1L;
        Visit visit = new Visit();
        Doctor doctor = new Doctor();
        doctor.setEmail("other@example.com");
        visit.setDoctor(doctor);

        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));

        // When & Then
        assertThrows(VisitException.class, () ->
                visitService.cancelVisit(visitId, "doctor@example.com"));
    }

    @Test
    void cancelVisit_shouldThrowWhenVisitInPast() {
        // Given
        Long visitId = 1L;
        String doctorEmail = "doctor@example.com";
        Visit visit = new Visit();
        Doctor doctor = new Doctor();
        doctor.setEmail(doctorEmail);
        visit.setDoctor(doctor);
        visit.setStartTime(LocalDateTime.now().minusHours(1));

        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));

        // When & Then
        assertThrows(VisitException.class, () ->
                visitService.cancelVisit(visitId, doctorEmail));
    }

    @Test
    void reserveVisit_shouldReserveVisitWhenDataValid() {
        // Given
        Long visitId = 1L;
        String patientEmail = "patient@example.com";
        Visit visit = new Visit();
        Patient patient = new Patient();
        patient.setEmail(patientEmail);

        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(patientRepository.findByEmail(patientEmail)).thenReturn(Optional.of(patient));
        when(visitRepository.save(any(Visit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Visit result = visitService.reserveVisit(visitId, patientEmail);

        // Then
        assertNotNull(result);
        assertEquals(patient, result.getPatient());
        verify(visitRepository).save(visit);
    }
}
