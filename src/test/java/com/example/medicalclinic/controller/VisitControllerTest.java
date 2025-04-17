package com.example.medicalclinic.controller;

import com.example.medicalclinic.exception.VisitException;
import com.example.medicalclinic.model.BookVisitCommand;
import com.example.medicalclinic.model.CreateVisitCommand;
import com.example.medicalclinic.model.dto.DoctorDTO;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.model.dto.VisitDTO;
import com.example.medicalclinic.model.entity.Visit;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

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
    void createVisit_ShouldReturnCreatedVisit() throws Exception {
        // Given
        DoctorDTO doctor = DoctorDTO.builder().build();

        CreateVisitCommand command = CreateVisitCommand.builder()
                .doctorId(1L)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        VisitDTO expectedVisit = VisitDTO.builder()
                .id(1L)
                .doctor(doctor)
                .startTime(command.startTime())
                .endTime(command.endTime())
                .build();

        when(visitService.createVisit(
                eq(command.doctorId()),
                eq(command.startTime()),
                eq(command.endTime())
        )).thenReturn(expectedVisit);

        // When & Then
        mockMvc.perform(post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void bookVisit_ShouldReturnBookedVisit() throws Exception {
        // Given
        DoctorDTO doctor = DoctorDTO.builder().build();

        BookVisitCommand command = BookVisitCommand.builder()
                .visitId(1L)
                .patientId(1L)
                .build();

        VisitDTO expectedVisit = VisitDTO.builder()
                .id(1L)
                .doctor(doctor)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        when(visitService.bookVisit(
                eq(command.visitId()),
                eq(command.patientId())
        )).thenReturn(expectedVisit);

        // When & Then
        mockMvc.perform(post("/visits/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void getVisits_ShouldReturnPageOfVisits() throws Exception {
        // Given
        DoctorDTO doctor = DoctorDTO.builder().build();
        Pageable pageable = PageRequest.of(0, 10);

        VisitDTO visit = VisitDTO.builder()
                .id(1L)
                .doctor(doctor)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        Page<VisitDTO> page = new PageImpl<>(List.of(visit), pageable, 1);

        when(visitService.getVisits(
                any(),
                any()
        )).thenReturn(new PageableContentDTO<>(page.getTotalPages(), page.getTotalElements(), page.getNumber(), page.getContent()));

        // When & Then
        mockMvc.perform(get("/visits")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void reserveVisit_ShouldReturnReservedVisit() throws Exception {
        // Given
        Long visitId = 1L;
        String patientEmail = "patient@example.com";

        Visit visit = Visit.builder()
                .id(visitId)
                .build();

        when(visitService.reserveVisit(
                eq(visitId),
                eq(patientEmail)
        )).thenReturn(visit);

        // When & Then
        mockMvc.perform(post("/visits/{id}/reserve", visitId)
                        .param("patientEmail", patientEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(visitId));
    }

    @Test
    void cancelVisit_ShouldReturnOk() throws Exception {
        // Given
        Long visitId = 1L;
        String doctorEmail = "doctor@example.com";

        // When & Then
        mockMvc.perform(delete("/visits/cancel/{id}", visitId)
                        .param("doctorEmail", doctorEmail))
                .andExpect(status().isOk());
    }

    @Test
    void bookVisit_WithNonExistingVisit_ShouldReturnNotFound() throws Exception {
        // Given
        BookVisitCommand command = BookVisitCommand.builder()
                .visitId(999L)
                .patientId(1L)
                .build();

        when(visitService.bookVisit(
                eq(command.visitId()),
                eq(command.patientId())
        )).thenThrow(new VisitException("Visit not found", HttpStatus.NOT_FOUND));

        // When & Then
        mockMvc.perform(post("/visits/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound());
    }
}
