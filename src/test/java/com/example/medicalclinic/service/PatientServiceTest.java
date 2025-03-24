package com.example.medicalclinic.service;

import com.example.medicalclinic.exception.PatientException;
import com.example.medicalclinic.mapper.PatientMapper;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.model.dto.PatientDTO;
import com.example.medicalclinic.model.entity.Patient;
import com.example.medicalclinic.repository.PatientRepository;
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
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {
    private PatientRepository patientRepository;
    private PatientService patientService;
    private PatientMapper patientMapper;

    @BeforeEach
    public void setUp() {
        this.patientRepository = Mockito.mock(PatientRepository.class);
        this.patientMapper = Mappers.getMapper(PatientMapper.class);
        this.patientService = new PatientService(patientRepository, patientMapper);
    }

    @Test
    public void getAllPatients_PatientExist_PatientFound() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Patient> patientList = List.of(
                createPatient("123", "test@email.com"),
                createPatient("321", "test2@email.com")
        );
        Page<Patient> page = new PageImpl(patientList, pageable, 2L);
        when(patientRepository.findAll(pageable)).thenReturn(page);

        // When
        PageableContentDTO<PatientDTO> result = patientService.getAllPatients(pageable);

        //Then
        assertEquals(page.getTotalPages(), result.totalPages());
        assertEquals(page.getTotalElements(), result.totalElements());
        assertEquals(page.getContent().size(), result.content().size());
        assertEquals("123", result.content().get(0).getIdCardNo());
        assertEquals("test@email.com", result.content().get(0).getEmail());
        assertEquals("321", result.content().get(1).getIdCardNo());
        assertEquals("test2@email.com", result.content().get(1).getEmail());
    }

    @Test
    public void editPatientByEmail_PatientExists_DataChanged() {
        // Given
        String email = "test@gmail.com";
        Patient newPatient = createPatient("nowy@email.com", "Normalny", "Czlowiek");
        Patient currentPatient = createPatient("stary@gmail.com", "Gosciu", "Gosciowy");
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(currentPatient));
        when(patientRepository.save(any())).thenReturn(currentPatient);

        // when
        PatientDTO result = patientService.editPatientByEmail(email, newPatient);

        //then
        assertEquals("nowy@email.com", result.getEmail());
        assertEquals("Normalny Czlowiek", result.getFullName());
    }

    @Test
    public void editPatientByEmail_patientNotFound_throwsException() {
        // Given
        String email = "test@email.com";
        Patient patient = createPatient("123", "email@email.com");
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        PatientException exception = assertThrows(PatientException.class, () -> patientService.editPatientByEmail(email, patient));

        // Then
        assertEquals("Patient doesnt exist", exception.getMessage());
    }

    @Test
    public void getPatientByEmail_PatientExists_PatientFound() {
        // Given
        String email = "test@email.com";
        Patient currentPatient = createPatient("test@email.com", "Gosciu", "Gosciowy");

        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(currentPatient));

        // When
        PatientDTO result = patientService.getPatientByEmail(email);

        // Then
        assertEquals("test@email.com", result.getEmail());
    }

    @Test
    public void getPatientByEmail_patientNotFound_throwsException() {
        // Given
        String email = "test@email.com";
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        PatientException exception = assertThrows(PatientException.class, () -> patientService.getPatientByEmail(email));

        // Then
        assertEquals("Patient doesnt exist", exception.getMessage());
    }

    @Test
    public void changePassword_PatientExists_DataChanged() {
        // Given
        String email = "test@email.com";
        String password = "examplePassword";
        Patient currentPatient = createPatient(1L, "test@email.com", "passwordBefore");

        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(currentPatient));
        when(patientRepository.save(any())).thenReturn(currentPatient);

        // When
        Patient result = patientService.changePassword(email, password);

        // Then
        assertEquals("examplePassword", result.getPassword());
    }

    @Test
    public void changePassword_patientNotFound_throwsException() {
        // Given
        String email = "test@email.com";
        String password = "password";
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        PatientException exception = assertThrows(PatientException.class, () -> patientService.changePassword(email, password));

        // Then
        assertEquals("Patient doesnt exist", exception.getMessage());
    }

    @Test
    public void addPatient_PatientExists_PatientAdded() {
        // Given
        String email = "test@email.com";
        String idCardNo = "998";
        Patient newPatient = createPatient(1L, "name", "surname", "email@email.com", "12345");
        Patient existingPatient = createPatient("123", "existing@email.com");

        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(existingPatient));
        when(patientRepository.findByIdCardNo(idCardNo)).thenReturn(Optional.of(existingPatient));
        when(patientRepository.save(any())).thenReturn(newPatient);

        // When
        Patient result = patientService.addPatient(newPatient);

        // Then
        assertEquals(newPatient, result);
    }

    @Test
    public void addPatient_emailAlreadyExists_throwsException() {
        // Given
        Patient patient = createPatient(1L, "name", "surname", "email@email.com", "123");
        when(patientRepository.findByEmail(patient.getEmail())).thenReturn(Optional.of(Patient.builder().build()));

        // When
        PatientException exception = assertThrows(PatientException.class, () -> patientService.addPatient(patient));

        // Then
        assertEquals("Patient with email: " + patient.getEmail() + " already exists", exception.getMessage());
    }

    @Test
    public void addPatient_idCardAlreadyExists_throwsException() {
        // Given
        Patient patient = createPatient(1L, "name", "surname", "email@email.com", "123");
        when(patientRepository.findByIdCardNo(patient.getIdCardNo())).thenReturn(Optional.of(Patient.builder().build()));

        // When
        PatientException exception = assertThrows(PatientException.class, () -> patientService.addPatient(patient));

        // Then
        assertEquals("Patient with IdCardNo: " + patient.getIdCardNo() + " already exists", exception.getMessage());
    }

    @Test
    public void removePatientByEmail_PatientExists_PatientRemoved() {
        // Given
        String email = "test@email.com";
        Patient patient = createPatient("123", email);
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(patient));

        // When
        patientService.removePatientByEmail(email);

        // Then
        verify(patientRepository).delete(patient);
    }

    @Test
    public void removePatientByEmail_patientNotFound_throwsException() {
        // Given
        String email = "test@email.com";
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        PatientException exception = assertThrows(PatientException.class, () -> patientService.removePatientByEmail(email));

        // Then
        assertEquals("Patient doesnt exist", exception.getMessage());
    }

    private Patient createPatient(String idCardNo, String email) {
        return Patient.builder()
                .idCardNo(idCardNo)
                .email(email)
                .build();
    }

    private Patient createPatient(String email, String firstName, String lastName) {
        return Patient.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

    private Patient createPatient(Long id, String email, String password) {
        return Patient.builder()
                .id(id)
                .email(email)
                .password(password)
                .build();
    }

    private Patient createPatient(Long id, String firstName, String lastName, String email, String idCardNo) {
        return Patient.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .idCardNo(idCardNo)
                .email(email)
                .build();
    }
}
