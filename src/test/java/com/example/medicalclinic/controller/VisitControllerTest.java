package com.example.medicalclinic.controller;

import com.example.medicalclinic.exception.DoctorException;
import com.example.medicalclinic.exception.PatientException;
import com.example.medicalclinic.exception.VisitException;
import com.example.medicalclinic.model.CreateVisitCommand;
import com.example.medicalclinic.model.dto.DoctorDTO;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.model.dto.VisitDTO;
import com.example.medicalclinic.model.entity.Visit;
import com.example.medicalclinic.repository.DoctorRepository;
import com.example.medicalclinic.repository.VisitRepository;
import com.example.medicalclinic.service.VisitService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class VisitControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private VisitService visitService;

    @Test
    void getVisits_whenFound_thenReturnJson() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        VisitDTO visit1 = createVisitDto(1L, "test@email.com");
        VisitDTO visit2 = createVisitDto(2L, "test2@email.com");
        List<VisitDTO> visits = List.of(visit1, visit2);

        Page<VisitDTO> page = new PageImpl<>(visits, pageable, 2L);

        PageableContentDTO<VisitDTO> response = PageableContentDTO.from(page, visits);

        when(visitService.getVisits(pageable)).thenReturn(response);

        mockMvc.perform(get("/visits")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(2)))
                .andExpect(jsonPath("$.content[0].id", is(visit1.getId().intValue())))
                .andExpect(jsonPath("$.content[1].id", is(visit2.getId().intValue())))
                .andExpect(jsonPath("$.content[0].doctor.email", is(visit1.getDoctor().getEmail())))
                .andExpect(jsonPath("$.content[1].doctor.email", is(visit2.getDoctor().getEmail())))
                .andExpect(jsonPath("$.currentPage", is(0)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }

    @Test
    void createVisit_whenAdded_thenReturnJson() throws Exception {
        Long doctorId = 1L;
        LocalDateTime startTime = LocalDateTime.now().plusMinutes(30);
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(60);

        VisitDTO expectedVisit = createVisitDto(1L, "doctor@example.com");

        CreateVisitCommand command = new CreateVisitCommand(doctorId, startTime, endTime);

        when(visitService.createVisit(eq(doctorId), eq(startTime), eq(endTime))).thenReturn(expectedVisit);

        mockMvc.perform(post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(expectedVisit.getId().intValue())))
                .andExpect(jsonPath("$.doctor.email", is(expectedVisit.getDoctor().getEmail())))
                .andExpect(jsonPath("$.startTime").exists())
                .andExpect(jsonPath("$.endTime").exists());
    }

    @Test
    void createVisit_whenDoctorNotFound_thenThrowException() throws Exception {
        Long doctorId = 1L;
        LocalDateTime startTime = LocalDateTime.now().plusMinutes(30);
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(60);
        CreateVisitCommand command = new CreateVisitCommand(doctorId, startTime, endTime);

        when(visitService.createVisit(doctorId, startTime, endTime)).thenThrow(new DoctorException("Doctor doesnt exist"));

        mockMvc.perform(post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(jsonPath("$.message", is("Doctor doesnt exist")))
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.errorTime").exists());
    }

    @Test
    void bookVisit_whenBooked_thenReturnJson() throws Exception {
        Long visitId = 1L;
        Long patientId = 1L;
        VisitDTO visit = createVisitDto(visitId, "doctor@example.com");

        when(visitService.bookVisit(eq(visitId), eq(patientId))).thenReturn(visit);

        mockMvc.perform(post("/visits/book")
                        .param("visitId", visitId.toString())
                        .param("patientId", patientId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(visitId.intValue())))
                .andExpect(jsonPath("$.doctor.email", is("doctor@example.com")));
    }

    @Test
    void bookVisit_patientNotFound_thenThrowException() throws Exception {
        Long visitId = 1L;
        Long patientId = 1L;
        String errorMessage = "Patient doesnt exist";

        when(visitService.bookVisit(visitId, patientId)).thenThrow(new PatientException(errorMessage));

        mockMvc.perform(post("/visits/book")
                        .param("visitId", visitId.toString())
                        .param("patientId", patientId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(errorMessage)))
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.errorTime").exists());
    }

    @Test
    void bookVisit_visitNotFound_thenThrowException() throws Exception {
        Long visitId = 1L;
        Long patientId = 1L;
        String errorMessage = "Visit doesnt exist";

        when(visitService.bookVisit(visitId, patientId)).thenThrow(new VisitException(errorMessage));

        mockMvc.perform(post("/visits/book")
                        .param("visitId", visitId.toString())
                        .param("patientId", patientId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(errorMessage)))
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.errorTime").exists());
    }

    @Test
    void bookVisit_visitAlreadyBooked_thenThrowException() throws Exception {
        Long visitId = 1L;
        Long patientId = 1L;
        String errorMessage = "Visit is already booked";

        when(visitService.bookVisit(visitId, patientId)).thenThrow(new VisitException(errorMessage));

        mockMvc.perform(post("/visits/book")
                        .param("visitId", visitId.toString())
                        .param("patientId", patientId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(errorMessage)))
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.errorTime").exists());
    }

    private VisitDTO createVisitDto(Long visitId, String doctorEmail) {
        return VisitDTO.builder()
                .id(visitId)
                .doctor(DoctorDTO.builder()
                        .email(doctorEmail)
                        .facilityIds(List.of(1L, 2L, 3L))
                        .build())
                .startTime(LocalDateTime.now().plusMinutes(30))
                .endTime(LocalDateTime.now().plusMinutes(60))
                .build();
    }

    private Visit createVisit(LocalDateTime startTime, LocalDateTime endTime) {
        return Visit.builder()
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}
