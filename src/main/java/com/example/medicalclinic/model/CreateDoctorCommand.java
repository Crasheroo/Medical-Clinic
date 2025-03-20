package com.example.medicalclinic.model;

public record CreateDoctorCommand(
        String email,
        String password) {
}
