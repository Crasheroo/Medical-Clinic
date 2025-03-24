package com.example.medicalclinic.service;

import com.example.medicalclinic.exception.DoctorException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VisitServiceTest {
    private VisitRepository visitRepository;
    private VisitMapper visitMapper;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private VisitService visitService;

    @BeforeEach
    public void setUp() {
        this.visitRepository = Mockito.mock(VisitRepository.class);
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.patientRepository = Mockito.mock(PatientRepository.class);
        this.visitMapper = Mappers.getMapper(VisitMapper.class);
        this.visitService = new VisitService(visitRepository, visitMapper, doctorRepository, patientRepository);
    }

    @Test
    public void createVisit_visitExist_VisitCreated() {
        // Given
        Long doctorId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2025, 05, 12, 13, 00);
        LocalDateTime endTime = LocalDateTime.of(2025, 05, 12, 14, 00);

        Doctor doctor = createDoctor(doctorId);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(visitRepository.findByDoctorId(doctorId)).thenReturn(List.of());

        Visit visit = createVisit(doctor, startTime, endTime);

        when(visitRepository.save(any())).thenReturn(visit);

        // When
        VisitDTO result = visitService.createVisit(doctorId, startTime, endTime);

        // Then
        assertEquals(doctorId, result.getDoctor().getId());
        assertEquals(startTime, result.getStartTime());
        assertEquals(endTime, result.getEndTime());
    }

    @Test
    public void createVisit_doctorNotFound_throwsException() {
        // Given
        Long doctorId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2025, 5, 12, 13, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 5, 12, 14, 0);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        // When
        DoctorException exception = assertThrows(DoctorException.class, () -> visitService.createVisit(doctorId, startTime, endTime));

        // Then
        assertEquals("Doctor doesnt exist", exception.getMessage());
    }

    @Test
    public void createVisit_conflictingVisit_throwsException() {
        // Given
        Long doctorId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2025, 5, 12, 13, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 5, 12, 14, 0);
        Doctor doctor = createDoctor(doctorId);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        Visit conflictingVisit = createVisit(doctor, startTime.minusMinutes(30), endTime.plusMinutes(30));
        when(visitRepository.findByDoctorId(doctorId)).thenReturn(List.of(conflictingVisit));

        // When
        VisitException exception = assertThrows(VisitException.class, () -> visitService.createVisit(doctorId, startTime, endTime));

        // Then
        assertEquals("Doctor has a visit at this time", exception.getMessage());
    }

    @Test
    public void bookVisit_visitExist_VisitBooked() {
        // Given
        Long visitId = 1L;
        Long patientId = 1L;
        Visit visit = createVisit(visitId);
        Patient patient = createPatient(patientId);
        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        Visit savedVisit = createVisit(visitId);
        savedVisit.setPatient(patient);
        when(visitRepository.save(any())).thenReturn(savedVisit);

        // When
        VisitDTO result = visitService.bookVisit(visitId, patientId);

        // Then
        assertEquals(visitId, result.getId());
        assertEquals(visit.getStartTime(), result.getStartTime());
        assertEquals(visit.getEndTime(), result.getEndTime());
    }

    @Test
    public void bookVisit_visitNotFound_throwsException() {
        // Given
        Long visitId = 1L;
        Long patientId = 2L;
        when(visitRepository.findById(visitId)).thenReturn(Optional.empty());

        // When
        VisitException exception = assertThrows(VisitException.class, () -> visitService.bookVisit(visitId, patientId));

        // Then
        assertEquals("Visit doesnt exist", exception.getMessage());
    }

    @Test
    public void bookVisit_patientNotFound_throwsException() {
        // Given
        Long visitId = 1L;
        Long patientId = 1L;
        Visit visit = createVisit(visitId);

        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // When
        VisitException exception = assertThrows(VisitException.class, () -> visitService.bookVisit(visitId, patientId));

        // Then
        assertEquals("Patient doesnt exist", exception.getMessage());
    }

    @Test
    public void getVisits_visitsExists_VisitsFound() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Visit> visitList = List.of(
                createVisit(1L),
                createVisit(2L)
        );
        Page<Visit> page = new PageImpl(visitList, pageable, 2L);
        when(visitRepository.findAll(pageable)).thenReturn(page);

        // When
        PageableContentDTO<VisitDTO> result = visitService.getVisits(pageable);

        //Then
        assertEquals(page.getTotalPages(), result.totalPages());
        assertEquals(page.getTotalElements(), result.totalElements());
        assertEquals(page.getContent().size(), result.content().size());
        assertEquals(1L, result.content().get(0).getId());
        assertEquals(2L, result.content().get(1).getId());
    }

    private Visit createVisit(Long visitId) {
        return Visit.builder()
                .id(visitId)
                .build();
    }

    private Visit createVisit(Doctor doctor, LocalDateTime startTime, LocalDateTime endTime) {
        return Visit.builder()
                .doctor(doctor)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    private Doctor createDoctor(Long doctorId) {
        return Doctor.builder()
                .id(doctorId)
                .email("test@email.com")
                .password("password")
                .facilities(new HashSet<>())
                .build();
    }

    private Patient createPatient(Long patientId) {
        return Patient.builder()
                .id(patientId)
                .build();
    }
}
