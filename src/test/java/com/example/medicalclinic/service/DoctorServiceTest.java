package com.example.medicalclinic.service;

import com.example.medicalclinic.exception.DoctorException;
import com.example.medicalclinic.exception.FacilityException;
import com.example.medicalclinic.mapper.DoctorMapper;
import com.example.medicalclinic.model.dto.DoctorDTO;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.model.entity.Doctor;
import com.example.medicalclinic.model.entity.Facility;
import com.example.medicalclinic.repository.DoctorRepository;
import com.example.medicalclinic.repository.FacilityRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {
    private DoctorRepository doctorRepository;
    private FacilityRepository facilityRepository;
    private DoctorMapper doctorMapper;
    private DoctorService doctorService;

    @BeforeEach
    void setUp() {
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.facilityRepository = Mockito.mock(FacilityRepository.class);
        this.doctorMapper = Mappers.getMapper(DoctorMapper.class);
        this.doctorService = new DoctorService(doctorRepository, facilityRepository, doctorMapper);
    }

    @Test
    void getAllDoctors_DoctorsExist_DoctorsFound() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Doctor> doctorList = List.of(
                createDoctor(1L, "test@email.com", "password"),
                createDoctor(2L, "test2@email.com", "password")
        );
        Page<Doctor> page = new PageImpl(doctorList, pageable, 2L);
        when(doctorRepository.findAll(pageable)).thenReturn(page);

        // When
        PageableContentDTO<DoctorDTO> result = doctorService.getAllDoctors(pageable);

        //Then
        assertEquals(page.getTotalPages(), result.totalPages());
        assertEquals(page.getTotalElements(), result.totalElements());
        assertEquals(page.getContent().size(), result.content().size());
        assertEquals(1L, result.content().get(0).getId());
        assertEquals("test@email.com", result.content().get(0).getEmail());
        assertEquals(2L, result.content().get(1).getId());
        assertEquals("test2@email.com", result.content().get(1).getEmail());
    }

    @Test
    void addDoctor_DoctorDoesntExists_DoctorAdded() {
        // Given
        Doctor doctor = createDoctor(2L);
        when(doctorRepository.findByEmail(doctor.getEmail())).thenReturn(Optional.empty());
        when(doctorRepository.save(any())).thenReturn(doctor);

        // When
        Doctor result = doctorService.addDoctor(doctorMapper.toDTO(doctor));

        // Then
        assertEquals(doctor.getId(), result.getId());
    }

    @Test
    void addDoctor_doctorNotFound_throwsException() {
        // Given
        Doctor doctor = createDoctor("test@email.com");
        when(doctorRepository.findByEmail(doctor.getEmail())).thenReturn(Optional.of(doctor));

        // When
        DoctorException exception = assertThrows(DoctorException.class, () -> doctorService.addDoctor(doctorMapper.toDTO(doctor)));

        // Then
        assertEquals("Doctor with email: " + doctor.getEmail() + " already exists", exception.getMessage());
    }

    @Test
    void editDoctorByEmail_DoctorExists_DataChanged() {
        // Given
        String email = "test@email.com";
        Doctor newDoctor = createDoctor("new@email.com", "newPassword");
        Doctor currentDoctor = createDoctor("old@email.com", "password");

        when(doctorRepository.findByEmail(email)).thenReturn(Optional.of(currentDoctor));
        when(doctorRepository.save(any())).thenReturn(currentDoctor);

        // When
        DoctorDTO result = doctorService.editDoctorByEmail(email, newDoctor);

        // Then
        assertEquals("new@email.com", result.getEmail());
    }

    @Test
    void editDoctorByEmail_doctorNotFound_throwsException() {
        // Given
        String email = "email@email.com";
        Doctor doctor = createDoctor("test@email.com", "password");
        when(doctorRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        DoctorException exception = assertThrows(DoctorException.class, () -> doctorService.editDoctorByEmail(email, doctor));

        // Then
        assertEquals("Doctor doesnt exist", exception.getMessage());
    }

    @Test
    void removeDoctorByEmail_DoctorExists_DoctorRemoved() {
        // Given
        String email = "test@email.com";
        Doctor doctor = createDoctor(email);
        when(doctorRepository.findByEmail(email)).thenReturn(Optional.of(doctor));

        // When
        doctorService.removeDoctorByEmail(email);

        // Then
        verify(doctorRepository).delete(doctor);
    }

    @Test
    void removeDoctorByEmail_doctorNotFound_throwsException() {
        // Given
        String email = "test@email.com";
        when(doctorRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        DoctorException exception = assertThrows(DoctorException.class, () -> doctorService.removeDoctorByEmail(email));

        // Then
        assertEquals("Doctor doesnt exist", exception.getMessage());
    }

    @Test
    void getDoctorByEmail_DoctorExists_DoctorFound() {
        // Given
        String email = "test@email.com";
        Doctor currentDoctor = createDoctor("test@email.com", "password");
        when(doctorRepository.findByEmail(email)).thenReturn(Optional.of(currentDoctor));

        // When
        DoctorDTO result = doctorService.getDoctorByEmail(email);

        //Then
        assertEquals("test@gmail.com", result.getEmail());
    }

    @Test
    void getDoctorByEmail_doctorNotFound_throwsException() {
        // Given
        String email = "test@email.com";
        when(doctorRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        DoctorException exception = assertThrows(DoctorException.class, () -> doctorService.removeDoctorByEmail(email));

        // Then
        assertEquals("Doctor doesnt exist", exception.getMessage());
    }

    @Test
    void assignDoctorToFacility_DoctorAndFacilityExists_DoctorAssigned() {
        // Given
        Long doctorId = 1L;
        Long facilityId = 1L;
        Doctor doctor = createDoctor(doctorId);
        Facility facility = createFacility(facilityId, "testName");

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(facility));
        doctor.getFacilities().add(facility);
        when(doctorRepository.save(doctor)).thenReturn(doctor);

        // When
        DoctorDTO result = doctorService.assignDoctorToFacility(doctorId, facilityId);

        // Then
        assertTrue(result.getFacilityIds().contains(facilityId));
    }

    @Test
    void assignDoctorToFacility_doctorNotFound_throwsException() {
        // Given
        Long doctorId = 1L;
        Long facilityId = 1L;
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        // When
        DoctorException exception = assertThrows(DoctorException.class, () -> doctorService.assignDoctorToFacility(doctorId, facilityId));

        // Then
        assertEquals("Doctor doesnt exist", exception.getMessage());
    }

    @Test
    void assignDoctorToFacility_facilityNotFound_throwsException() {
        // Given
        Long doctorId = 1L;
        Long facilityId = 1L;
        Doctor doctor = createDoctor(doctorId);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());

        // When
        FacilityException exception = assertThrows(FacilityException.class, () -> doctorService.assignDoctorToFacility(doctorId, facilityId));

        // Then
        assertEquals("Facility doesnt exist", exception.getMessage());
    }

    @Test
    void changePassword_DoctorExists_DataChanged() {
        // Given
        String email = "test@email.com";
        String password = "newPassword";
        Doctor currentDoctor = createDoctor("test@email.com", "oldPassword");
        when(doctorRepository.findByEmail(email)).thenReturn(Optional.of(currentDoctor));
        when(doctorRepository.save(any())).thenReturn(currentDoctor);

        // When
        Doctor result = doctorService.changePassword(email, password);

        // Then
        assertEquals("newPassword", result.getPassword());
    }

    @Test
    void changePassword_doctorNotFound_throwsException() {
        // Given
        String email = "test@email.com";
        String password = "password";
        when(doctorRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        DoctorException exception = assertThrows(DoctorException.class, () -> doctorService.removeDoctorByEmail(email));

        //Then
        assertEquals("Doctor doesnt exist", exception.getMessage());
    }

    @Test
    void removeFacilityFromDoctor_doctorFound_facilityRemoved() {
        // Given
        Long doctorId = 1L;
        Long facilityId = 1L;
        Doctor doctor = createDoctor(2L);
        Facility facility = createFacility(2L, "testName");
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(facility));
        doctor.getFacilities().add(facility);
        when(doctorRepository.save(doctor)).thenReturn(doctor);

        //When
        doctorService.removeFacilityFromDoctor(doctorId, facilityId);

        // Then
        assertFalse(doctor.getFacilities().contains(facility));
    }

    @Test
    void removeFacilityFromDoctor_doctorNotFound_throwsException() {
        // Given
        Long doctorId = 1L;
        Long facilityId = 1L;

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        // When
        DoctorException exception = assertThrows(DoctorException.class, () -> doctorService.removeFacilityFromDoctor(doctorId, facilityId));

        // Then
        assertEquals("Doctor doesnt exist", exception.getMessage());
    }

    @Test
    void removeFacilityFromDoctor_facilityNotFound_throwsException() {
        // Given
        Long doctorId = 1L;
        Long facilityId = 1L;
        Doctor doctor = createDoctor(doctorId);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());

        // When
        FacilityException exception = assertThrows(FacilityException.class, () -> doctorService.removeFacilityFromDoctor(doctorId, facilityId));

        // Then
        assertEquals("Facility doesnt exist", exception.getMessage());
    }

    private Facility createFacility(Long id, String facilityName) {
        return Facility.builder()
                .id(id)
                .facilityName(facilityName)
                .build();
    }

    private Doctor createDoctor(String email) {
        return Doctor.builder()
                .email(email)
                .build();
    }

    private Doctor createDoctor(String email, String password) {
        return Doctor.builder()
                .email(email)
                .password(password)
                .build();
    }

    private Doctor createDoctor(Long id, String email, String password) {
        return Doctor.builder()
                .id(id)
                .email(email)
                .password(password)
                .build();
    }

    private Doctor createDoctor(Long id) {
        return Doctor.builder()
                .id(id)
                .email("test@email.com")
                .password("password")
                .facilities(new HashSet<>())
                .build();
    }
}
