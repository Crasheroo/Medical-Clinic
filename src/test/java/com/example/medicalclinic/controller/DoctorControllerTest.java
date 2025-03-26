package com.example.medicalclinic.controller;

import com.example.medicalclinic.model.CreateDoctorCommand;
import com.example.medicalclinic.model.dto.DoctorDTO;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.service.DoctorService;
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

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DoctorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private DoctorService doctorService;

    @Test
    void getDoctors_whenFound_thenReturnJson() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        DoctorDTO doctor1 = createDoctorDto(1L, "email1@email.com");
        DoctorDTO doctor2 = createDoctorDto(2L, "email2@email.com");
        List<DoctorDTO> doctors = List.of(doctor1, doctor2);

        Page<DoctorDTO> page = new PageImpl<>(doctors, pageable, 2L);

        PageableContentDTO<DoctorDTO> response = PageableContentDTO.from(page, doctors);

        when(doctorService.getAllDoctors(pageable)).thenReturn(response);

        mockMvc.perform(get("/doctors")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(2)))
                .andExpect(jsonPath("$.content[0].email", is(doctor1.getEmail())))
                .andExpect(jsonPath("$.content[1].email", is(doctor2.getEmail())))
                .andExpect(jsonPath("$.currentPage", is(0)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }

    @Test
    void getDoctorByEmail_whenFound_thenReturnJson() throws Exception {
        String email = "test@email.com";
        DoctorDTO doctor = createDoctorDto(1L, email);

        when(doctorService.getDoctorByEmail(email)).thenReturn(doctor);

        mockMvc.perform(get("/doctors/{email}", email)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(email)));
    }

    @Test
    void addDoctor_whenAdd_thenReturnJson() throws Exception {
        Long doctorId = 1L;
        String email = "email@email.com";
        String password = "password";

        CreateDoctorCommand command = new CreateDoctorCommand(doctorId, email, password);
        DoctorDTO doctor = createDoctorDto(doctorId ,email);

        when(doctorService.addDoctor(eq(command))).thenReturn(doctor);

        mockMvc.perform(post("/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(doctor.getId().intValue())))
                .andExpect(jsonPath("$.email", is(email)))
                .andExpect(jsonPath("$.facilityIds").isArray());
    }

    @Test
    

    private DoctorDTO createDoctorDto(Long id, String email) {
        return DoctorDTO.builder()
                .id(id)
                .email(email)
                .facilityIds(List.of(1L, 2L, 3L))
                .build();
    }
}
