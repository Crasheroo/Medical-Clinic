package com.example.medicalclinic.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateFacilityRequest {
    private String facilityName;
    private String city;
    private String postcode;
    private String street;
    private String buildingNumber;
    private List<CreateDoctorRequest> doctors;
}
