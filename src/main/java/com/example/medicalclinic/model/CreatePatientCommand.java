package com.example.medicalclinic.model;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CreatePatientCommand(String email, String password, String idCardNo, String firstName, String lastName,
                                   String phoneNumber, LocalDate birthday) {
}
