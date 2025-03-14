package com.example.medicalclinic.dto;

import lombok.Data;

@Data
public class CreateDoctorRequest {
    private String email;
    private String password;
}
