package com.example.medicalclinic.dto;

import com.example.medicalclinic.model.Doctor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class FacilityRequestDTO {
    private String facilityName;
    private String city;
    private String postcode;
    private String street;
    private String buildingNumber;
    private List<DoctorRequestDTO> doctors;
}
