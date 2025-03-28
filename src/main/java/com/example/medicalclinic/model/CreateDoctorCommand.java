package com.example.medicalclinic.model;

import lombok.Builder;


@Builder
public record CreateDoctorCommand(
        Long id,
        String email,
        String password) {
}
