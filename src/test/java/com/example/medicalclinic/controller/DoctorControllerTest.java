package com.example.medicalclinic.controller;

import com.example.medicalclinic.exception.DoctorException;
import com.example.medicalclinic.exception.FacilityException;
import com.example.medicalclinic.exception.PatientException;
import com.example.medicalclinic.model.CreateDoctorCommand;
import com.example.medicalclinic.model.dto.DoctorDTO;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.model.dto.PatientDTO;
import com.example.medicalclinic.model.entity.Doctor;
import com.example.medicalclinic.model.entity.Facility;
import com.example.medicalclinic.model.entity.Patient;
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

import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    void getDoctorByEmail_whenFound_return200() throws Exception {
        String email = "test@email.com";
        DoctorDTO doctor = createDoctorDto(1L, email);

        when(doctorService.getDoctorByEmail(email)).thenReturn(doctor);

        mockMvc.perform(get("/doctors/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(email)));
    }

    @Test
    void getDoctorByEmail_doctorNotFound_returnException() throws Exception {
        String email = "test@email.com";
        String errorMessage = "Doctor doesnt exist";

        when(doctorService.getDoctorByEmail(email)).thenThrow(new DoctorException(errorMessage));

        mockMvc.perform(get("/doctors/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(errorMessage)))
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.errorTime").exists());
    }

    @Test
    void addDoctor_whenAdd_thenReturnJson() throws Exception {
        Long doctorId = 1L;
        String email = "email@email.com";
        String password = "password";

        CreateDoctorCommand command = new CreateDoctorCommand(doctorId, email, password);
        DoctorDTO doctor = createDoctorDto(doctorId, email);

        when(doctorService.addDoctor(command)).thenReturn(doctor);

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
    void addDoctor_doctorAlreadyExist_throwException() throws Exception {
        Long doctorId = 1L;
        String email = "email@email.com";
        String password = "password";
        String errorMessage = "Doctor already exist";

        CreateDoctorCommand command = new CreateDoctorCommand(doctorId, email, password);

        when(doctorService.addDoctor(any())).thenThrow(new DoctorException(errorMessage));

        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(jsonPath("$.message", is(errorMessage)))
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.errorTime").exists());
    }

    @Test
    void editDoctor_doctorFound_return200() throws Exception {
        String email = "test@email.com";
        Long doctorId = 1L;
        String newEmail = "updated@email.com";
        String newPassword = "newPassword";

        CreateDoctorCommand doctorCommand = new CreateDoctorCommand(doctorId, newEmail, newPassword);
        DoctorDTO doctor = createDoctorDto(doctorId, newEmail);

        when(doctorService.editDoctorByEmail(email, doctorCommand)).thenReturn(doctor);

        mockMvc.perform(put("/doctors/{email}", email)
                        .content(objectMapper.writeValueAsString(doctorCommand))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(doctorId.intValue())))
                .andExpect(jsonPath("$.email", is(newEmail)));
    }

    @Test
    void editDoctor_doctorNotFound_throwException() throws Exception {
        String existingEmail = "doctor@example.com";
        String newEmail = "taken@email.com";
        CreateDoctorCommand command = new CreateDoctorCommand(1L, newEmail, "password");

        when(doctorService.editDoctorByEmail(existingEmail, command)).thenThrow(new DoctorException("Email is taken"));

        mockMvc.perform(put("/doctors/{email}", existingEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(jsonPath("$.message", is("Email is taken")))
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.errorTime").exists());
    }

    @Test
    void removeDoctor_doctorRemoved_return200() throws Exception {
        String email = "test@example.com";
        DoctorDTO doctor = createDoctorDto(1L, email);

        when(doctorService.getDoctorByEmail(email)).thenReturn(doctor);

        mockMvc.perform(delete("/doctors/{email}", email)
                        .content(objectMapper.writeValueAsString(doctor))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeDoctor_doctorNotFound_throwsException() throws Exception {
        String email = "test@email.com";
        String errorMessage = "Doctor doesnt exist";

        doThrow(new DoctorException(errorMessage)).when(doctorService).removeDoctorByEmail(email);

        mockMvc.perform(delete("/doctors/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)))
                .andExpect(jsonPath("$.errorTime").exists());
    }

    @Test
    void assignDoctorToFacility_doctorAndFacilityFound_return200() throws Exception {
        String email = "email@email.com";
        Long doctorId = 1L;
        Long facilityId = 1L;

        DoctorDTO doctorDTO = createDoctorDto(doctorId, email);

        when(doctorService.assignDoctorToFacility(doctorId, facilityId)).thenReturn(doctorDTO);

        mockMvc.perform(post("/doctors/{doctorId}/facilities/{facilityId}", doctorId, facilityId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(doctorId.intValue())))
                .andExpect(jsonPath("$.facilityIds[0]", is(facilityId.intValue())));
    }

    @Test
    void assignDoctorToFacility_doctorNotFound_throwsException() throws Exception {
        Long doctorId = 1L;
        Long facilityId = 2L;
        String errorMessage = "Doctor doesnt exist";

        when(doctorService.assignDoctorToFacility(doctorId, facilityId)).thenThrow(new DoctorException(errorMessage));

        mockMvc.perform(post("/doctors/{doctorId}/facilities/{facilityId}", doctorId, facilityId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)))
                .andExpect(jsonPath("$.errorTime").exists());
    }

    @Test
    void assignDoctorToFacility_facilityNotFound_throwsException() throws Exception {
        Long doctorId = 1L;
        Long facilityId = 2L;
        String errorMessage = "Facility doesnt exist";

        when(doctorService.assignDoctorToFacility(doctorId, facilityId)).thenThrow(new FacilityException(errorMessage));

        mockMvc.perform(post("/doctors/{doctorId}/facilities/{facilityId}", doctorId, facilityId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)))
                .andExpect(jsonPath("$.errorTime").exists());
    }

    @Test
    void removeFacilityFromDoctor_facilityRemoved_return200() throws Exception {
        Long doctorId = 1L;
        Long facilityId = 1L;

        doNothing().when(doctorService).removeFacilityFromDoctor(doctorId, facilityId);

        mockMvc.perform(delete("/doctors/{doctorId}/facilities/{facilityId}", doctorId, facilityId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void removeFacilityFromDoctor_facilityNotFound_throwsException() throws Exception {
        String errorMessage = "Facility doesnt exist";
        Long doctorId = 1L;
        Long facilityId = 1L;

        doThrow(new FacilityException(errorMessage)).when(doctorService).removeFacilityFromDoctor(doctorId, facilityId);

        mockMvc.perform(delete("/doctors/{doctorId}/facilities/{facilityId}", doctorId, facilityId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)))
                .andExpect(jsonPath("$.errorTime").exists());
    }

    @Test
    void removeFacilityFromDoctor_DoctorNotFound_throwsException() throws Exception {
        String errorMessage = "Doctor doesnt exist";
        Long doctorId = 1L;
        Long facilityId = 1L;

        doThrow(new DoctorException(errorMessage)).when(doctorService).removeFacilityFromDoctor(doctorId, facilityId);

        mockMvc.perform(delete("/doctors/{doctorId}/facilities/{facilityId}", doctorId, facilityId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)))
                .andExpect(jsonPath("$.errorTime").exists());
    }

    private DoctorDTO createDoctorDto(Long id, String email) {
        return DoctorDTO.builder()
                .id(id)
                .email(email)
                .facilityIds(List.of(1L, 2L, 3L))
                .build();
    }
}
