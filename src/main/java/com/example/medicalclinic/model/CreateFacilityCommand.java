package com.example.medicalclinic.model;

import lombok.Builder;

import java.util.List;

@Builder
public record CreateFacilityCommand(
        String facilityName,
        String city,
        String postcode,
        String street,
        String buildingNumber,
        List<CreateDoctorCommand> doctors) {
}
