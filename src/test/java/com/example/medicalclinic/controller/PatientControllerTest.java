package com.example.medicalclinic.controller;

import com.example.medicalclinic.model.ChangePasswordCommand;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.model.dto.PatientDTO;
import com.example.medicalclinic.model.entity.Patient;
import com.example.medicalclinic.service.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private PatientService patientService;

    @Test
    void getPatients_whenFound_thenReturnJson() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        PatientDTO patient1 = createPatientDto("email@email.com", "ID1", "123");
        PatientDTO patient2 = createPatientDto("emai2@email.com", "ID2", "321");
        List<PatientDTO> patients = List.of(patient1, patient2);

        Page<PatientDTO> page = new PageImpl<>(patients, pageable, 2L);

        PageableContentDTO<PatientDTO> response = PageableContentDTO.from(page, patients);

        when(patientService.getAllPatients(pageable)).thenReturn(response);

        mockMvc.perform(get("/patients")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(2)))
                .andExpect(jsonPath("$.content[0].email", is(patient1.getEmail())))
                .andExpect(jsonPath("$.content[1].email", is(patient2.getEmail())))
                .andExpect(jsonPath("$.currentPage", is(0)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }

    @Test
    void getPatientByEmail_whenFound_thenReturnJson() throws Exception {
        String email = "test@email.com";
        PatientDTO patientDto = createPatientDto(email, "123456");

        when(patientService.getPatientByEmail(email)).thenReturn(patientDto);

        mockMvc.perform(get("/patients/{email}", email)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(email)));
    }

    @Test
    void addPatient_whenAdd_thenReturnJson() throws Exception {
        Patient patient = createPatient("test@example.com", "ID123456");
        PatientDTO patientDTO = createPatientDto("test@example.com", "ID123456");

        when(patientService.addPatient(any())).thenReturn(patientDTO);

        mockMvc.perform(post("/patients")
                        .content(objectMapper.writeValueAsString(patient))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.fullName", is("John Doe")))
                .andExpect(jsonPath("$.idCardNo", is("ID123456")))
                .andExpect(jsonPath("$.birthday", is("2000-01-01")))
                .andExpect(jsonPath("$.phoneNumber", is("+1234567890")));
    }

    @Test
    void removePatient_whenDeleted_CosTam() throws Exception {
        String email = "test@example.com";
        PatientDTO patientDTO = createPatientDto(email, "ID123456");
        Patient patient = createPatient(email, "ID123456");

        when(patientService.getPatientByEmail(email)).thenReturn(patientDTO);

        mockMvc.perform(delete("/patients/{email}", email)
                        .content(objectMapper.writeValueAsString(patient))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void editPatient_whenPatientFound_DataChanged() throws Exception {
        String email = "test@email.com";
        PatientDTO patientDTO = createPatientDto("new@email.com", "ID123456", "654321");
        Patient patient = createPatient(email, "ID123456");

        when(patientService.editPatientByEmail(eq(email), any())).thenReturn(patientDTO);

        mockMvc.perform(put("/patients/{email}", email)
                .content(objectMapper.writeValueAsString(patient))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber", is("654321")))
                .andExpect(jsonPath("$.email", is("new@email.com")));
    }

    @Test
    void editPatientPassword_whenPatientFound_passwordChanged() throws Exception {
        String email = "test@email.com";
        String password = "newPassword";
        ChangePasswordCommand passwordCommand = new ChangePasswordCommand(password);
        Patient patient = createPatient(email, "ID123456");
        patient.setPassword(password);

        when(patientService.changePassword(email, password)).thenReturn(patient);

        mockMvc.perform(patch("/patients/{email}/password", email)
                .content(objectMapper.writeValueAsString(passwordCommand))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.password", is(password)));
    }

    private Patient createPatient(String email, String idCardNo) {
        return Patient.builder()
                .email(email)
                .password("securePassword123")
                .firstName("John")
                .lastName("Doe")
                .idCardNo(idCardNo)
                .birthday(LocalDate.of(2000, 1, 1))
                .phoneNumber("+1234567890")
                .build();
    }

    private PatientDTO createPatientDto(String email, String idCardNo) {
        return PatientDTO.builder()
                .fullName("John Doe")
                .email(email)
                .fullName("John Doe")
                .idCardNo(idCardNo)
                .birthday(LocalDate.of(2000, 1, 1))
                .phoneNumber("+1234567890")
                .build();
    }

    private PatientDTO createPatientDto(String email, String idCardNo, String phoneNumber) {
        return PatientDTO.builder()
                .fullName("John Doe")
                .email(email)
                .fullName("John Doe")
                .idCardNo(idCardNo)
                .birthday(LocalDate.of(2000, 1, 1))
                .phoneNumber(phoneNumber)
                .build();
    }
}
