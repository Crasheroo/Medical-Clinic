package com.example.medicalclinic.service;

import com.example.medicalclinic.exception.FacilityException;
import com.example.medicalclinic.mapper.FacilityMapper;
import com.example.medicalclinic.model.CreateDoctorCommand;
import com.example.medicalclinic.model.CreateFacilityCommand;
import com.example.medicalclinic.model.dto.FacilityDTO;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FacilityServiceTest {
    private FacilityRepository facilityRepository;
    private DoctorRepository doctorRepository;
    private FacilityMapper facilityMapper;
    private FacilityService facilityService;

    @BeforeEach
    public void setUp() {
        this.facilityRepository = Mockito.mock(FacilityRepository.class);
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.facilityMapper = Mappers.getMapper(FacilityMapper.class);
        this.facilityService = new FacilityService(facilityRepository, doctorRepository, facilityMapper);
    }

    @Test
    public void getAllFacilities_FacilitiesExist_FacilitiesFound() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Facility> facilityList = List.of(
                createFacility(1L, "testName1"),
                createFacility(2L, "testName2")
        );
        Page<Facility> page = new PageImpl(facilityList, pageable, 2L);
        when(facilityRepository.findAll(pageable)).thenReturn(page);

        // When
        PageableContentDTO<FacilityDTO> result = facilityService.getAllFacilities(pageable);

        //Then
        assertEquals(page.getTotalPages(), result.totalPages());
        assertEquals(page.getTotalElements(), result.totalElements());
        assertEquals(page.getContent().size(), result.content().size());
        assertEquals(1L, result.content().get(0).getId());
        assertEquals("testName1", result.content().get(0).getFacilityName());
        assertEquals(2L, result.content().get(1).getId());
        assertEquals("testName2", result.content().get(1).getFacilityName());
    }

    @Test
    public void getFacilityByName_FacilityExist_FacilityFound() {
        // Given
        String facilityName = "testName";
        Facility currentFacility = createFacility(1L, "testName");
        when(facilityRepository.findByFacilityName(facilityName)).thenReturn(Optional.of(currentFacility));

        // When
        Facility result = facilityService.getFacilityByName(facilityName);

        // Then
        assertEquals("testName", result.getFacilityName());
    }

    @Test
    public void getFacilityByName_facilityNotFound_throwsException() {
        // Given
        String facilityName = "testName";
        when(facilityRepository.findByFacilityName(facilityName)).thenReturn(Optional.empty());

        // When
        FacilityException exception = assertThrows(FacilityException.class, () -> facilityService.getFacilityByName(facilityName));

        // Then
        assertEquals("Facility doesnt exist", exception.getMessage());
    }

    @Test
    public void removeFacilityByName_FacilityExist_FacilityRemoved() {
        // Given
        String facilityName = "testName";
        Facility facility = createFacility(facilityName);
        when(facilityRepository.findByFacilityName(facilityName)).thenReturn(Optional.of(facility));

        // When
        facilityService.removeFacilityByName(facilityName);

        // Then
        verify(facilityRepository).delete(facility);
    }

    @Test
    public void removeFacilityByName_facilityNotFound_throwsException() {
        // Given
        String facilityName = "testName";
        when(facilityRepository.findByFacilityName(facilityName)).thenReturn(Optional.empty());

        // When
        FacilityException exception = assertThrows(FacilityException.class, () -> facilityService.removeFacilityByName(facilityName));

        // Then
        assertEquals("Facility doesnt exist", exception.getMessage());
    }

    @Test
    public void updateByName_FacilityExist_DataChanged() {
        // Given
        String facilityName = "testName";
        Facility newFacility = createFacility(1L, "newFacility");
        Facility currentFacility = createFacility(1L, "oldFacility");
        when(facilityRepository.findByFacilityName(facilityName)).thenReturn(Optional.of(currentFacility));
        when(facilityRepository.save(any())).thenReturn(currentFacility);

        // when
        Facility result = facilityService.updateByName(facilityName, newFacility);

        //then
        assertEquals(1L, result.getId());
        assertEquals("newFacility", result.getFacilityName());
    }

    @Test
    public void updateByName_facilityNotFound_throwsException() {
        // Given
        String facilityName = "testName";
        Facility facility = createFacility(1L, facilityName);
        when(facilityRepository.findByFacilityName(facilityName)).thenReturn(Optional.empty());

        // When
        FacilityException exception = assertThrows(FacilityException.class, () -> facilityService.updateByName(facilityName, facility));

        //Then
        assertEquals("Facility doesnt exist", exception.getMessage());
    }

    @Test
    public void saveFacilitiesWithDoctors_FacilitiesCreated_FacilitiesAdded() {
        // Given
        CreateDoctorCommand doctorCmd1 = CreateDoctorCommand.builder()
                .email("test@email.com")
                .build();
        CreateDoctorCommand doctorCmd2 = CreateDoctorCommand.builder()
                .email("test2@email.com")
                .build();

        CreateFacilityCommand facilityCmd1 = CreateFacilityCommand.builder()
                .facilityName("testName1")
                .doctors(List.of(doctorCmd1))
                .build();
        CreateFacilityCommand facilityCmd2 = CreateFacilityCommand.builder()
                .facilityName("testName2")
                .doctors(List.of(doctorCmd2))
                .build();

        Doctor doctor1 = Doctor.builder()
                .id(1L)
                .email("test@email.com")
                .build();
        Doctor doctor2 = Doctor.builder()
                .id(2L)
                .email("test2@email.com")
                .build();

        Facility facility1 = Facility.builder()
                .id(1L)
                .facilityName("Clinic A")
                .doctors(Set.of(doctor1))
                .build();

        Facility facility2 = Facility.builder()
                .id(2L)
                .facilityName("Clinic B")
                .doctors(Set.of(doctor2))
                .build();

        List<CreateFacilityCommand> request = List.of(facilityCmd1, facilityCmd2);
        when(facilityRepository.saveAll(any())).thenReturn(List.of(facility1, facility2));

        // When
        List<FacilityDTO> result = facilityService.saveFacilitiesWithDoctors(request);

        // Then
        assertEquals(2, result.size());
        assertEquals("Clinic A", result.get(0).getFacilityName());
        assertEquals("Clinic B", result.get(1).getFacilityName());
    }

    @Test
    public void saveFacilitiesWithDoctors_emptyList_throwsException() {
        //Given
        List<CreateFacilityCommand> request = List.of();

        // When
        List<FacilityDTO> result = facilityService.saveFacilitiesWithDoctors(request);

        //Then
        assertEquals(0, result.size());
    }

    private Facility createFacility(String facilityName) {
        return Facility.builder()
                .facilityName(facilityName)
                .doctors(new HashSet<>())
                .build();
    }

    private Facility createFacility(Long id, String facilityName) {
        return Facility.builder()
                .id(id)
                .facilityName(facilityName)
                .doctors(new HashSet<>())
                .build();
    }
}
