package com.example.medicalclinic.controller;

import com.example.medicalclinic.exception.FacilityException;
import com.example.medicalclinic.model.CreateFacilityCommand;
import com.example.medicalclinic.model.dto.DoctorDTO;
import com.example.medicalclinic.model.dto.FacilityDTO;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.model.entity.Facility;
import com.example.medicalclinic.service.DoctorService;
import com.example.medicalclinic.service.FacilityService;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FacilityControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private FacilityService facilityService;

    @Test
    void getFacilities_whenFound_return200() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        FacilityDTO facility1 = createFacilityDto(1L, "Hospital1");
        FacilityDTO facility2 = createFacilityDto(2L, "Hospital2");
        List<FacilityDTO> facilities = List.of(facility1, facility2);

        Page<FacilityDTO> page = new PageImpl<>(facilities, pageable, 2L);

        PageableContentDTO<FacilityDTO> response = PageableContentDTO.from(page, facilities);

        when(facilityService.getAllFacilities(pageable)).thenReturn(response);

        mockMvc.perform(get("/facilities")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(2)))
                .andExpect(jsonPath("$.content[0].facilityName", is(facility1.getFacilityName())))
                .andExpect(jsonPath("$.content[1].facilityName", is(facility2.getFacilityName())))
                .andExpect(jsonPath("$.currentPage", is(0)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }

    @Test
    void getFacilityByName_whenFound_return200() throws Exception {
        String facilityName = "Hospital";
        FacilityDTO facilityDto = createFacilityDto(1L, facilityName);

        when(facilityService.getFacilityByName(facilityName)).thenReturn(facilityDto);

        mockMvc.perform(get("/facilities/{facilityName}", facilityName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(facilityDto.getId().intValue())))
                .andExpect(jsonPath("$.facilityName", is(facilityDto.getFacilityName())))
                .andExpect(jsonPath("$.doctorIds").isArray());
    }

    @Test
    void getFacilityByName_facilityNotFound_throwsException() throws Exception {
        when(facilityService.getFacilityByName("name")).thenThrow(new FacilityException("Facility doesnt exist"));

        mockMvc.perform(get("/facilities/{facilityName}", "name")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Facility doesnt exist")))
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.errorTime").exists());
    }

    @Test
    void removeFacility_whenFound_return200() throws Exception {
        String facilityName = "name";
        FacilityDTO facilityDTO = createFacilityDto(1L, facilityName);

        when(facilityService.getFacilityByName(facilityName)).thenReturn(facilityDTO);

        mockMvc.perform(delete("/facilities/{facilityName}", facilityName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void removeFacility_facilityNotFound_throwsException() throws Exception {
        doThrow(new FacilityException("Facility doesnt exist")).when(facilityService).removeFacilityByName("name");

        mockMvc.perform(delete("/facilities/{facilityName}", "name")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Facility doesnt exist")))
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.errorTime").exists());
    }

    @Test
    void editFacility_whenFound_return200() throws Exception {
        FacilityDTO facilityDto = createFacilityDto(1L, "Hospital");
        Facility facility = createFacility(1L, "Hospitalll");

        when(facilityService.updateByName("Hospital", facility)).thenReturn(facilityDto);

        mockMvc.perform(put("/facilities/{facilityName}", "Hospital")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(facilityDto)))
                .andExpect(jsonPath("$.id", is(facilityDto.getId().intValue())))
                .andExpect(jsonPath("$.facilityName", is(facilityDto.getFacilityName())))
                .andExpect(jsonPath("$.doctorIds").isArray());
    }

    @Test
    void editFacility_facilityNotFound_throwsException() throws Exception {
        Facility facility = createFacility(1L, "name");
        when(facilityService.updateByName("name", facility)).thenThrow(new FacilityException("Facility doesnt exist"));

        mockMvc.perform(put("/facilities/{facilityName}", "name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(facility)))
                .andExpect(jsonPath("$.message", is("Facility doesnt exist")))
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.errorTime").exists());
    }

    @Test
    void createFacilitiesWithDoctors_whenFound_return200() throws Exception {
        CreateFacilityCommand facilityCommand = createFacilityCommand("facilityName");
        FacilityDTO facilityDTO = createFacilityDto(1L, "name");

        when(facilityService.saveFacilitiesWithDoctors(List.of(facilityCommand))).thenReturn(List.of(facilityDTO));

        mockMvc.perform(post("/facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(facilityDTO))))
                .andExpect(jsonPath("$[0].id", is(facilityDTO.getId().intValue())))
                .andExpect(jsonPath("$[0].facilityName", is(facilityDTO.getFacilityName())));
    }

    private FacilityDTO createFacilityDto(Long facilityId, String facilityName) {
        return FacilityDTO.builder()
                .id(facilityId)
                .facilityName(facilityName)
                .doctorIds(new HashSet<>())
                .build();
    }

    private Facility createFacility(Long facilityId, String facilityName) {
        return Facility.builder()
                .id(facilityId)
                .facilityName(facilityName)
                .doctors(new HashSet<>())
                .build();
    }

    private CreateFacilityCommand createFacilityCommand(String facilityName) {
        return CreateFacilityCommand.builder()
                .facilityName(facilityName)
                .city("city")
                .street("street")
                .buildingNumber("buildingNo")
                .doctors(new ArrayList<>())
                .build();
    }
}
