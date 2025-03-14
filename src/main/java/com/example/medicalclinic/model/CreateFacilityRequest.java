package com.example.medicalclinic.model;

import java.util.List;

public record CreateFacilityRequest(
        String facilityName,
        String city,
        String postcode,
        String street,
        String buildingNumber,
        List<CreateDoctorRequest> doctors) {
}
