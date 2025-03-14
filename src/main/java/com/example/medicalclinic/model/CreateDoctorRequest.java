package com.example.medicalclinic.model;

public record CreateDoctorRequest(
        String email,
        String password) {
}
