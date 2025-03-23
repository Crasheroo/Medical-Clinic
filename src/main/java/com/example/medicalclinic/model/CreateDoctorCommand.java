package com.example.medicalclinic.model;

import lombok.Builder;

@Builder
public record CreateDoctorCommand(
        String email,
        String password) {
}
