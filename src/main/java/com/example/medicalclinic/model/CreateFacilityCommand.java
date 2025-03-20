package com.example.medicalclinic.model;

import java.util.List;

public record CreateFacilityCommand(
        String facilityName,
        String city,
        String postcode,
        String street,
        String buildingNumber,
        List<CreateDoctorCommand> doctors) {
}
