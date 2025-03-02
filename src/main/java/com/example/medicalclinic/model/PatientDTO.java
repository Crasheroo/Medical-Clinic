package com.example.medicalclinic.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDTO {
    private String email;
    private String idCardNo;
    private String fullName;
    private String phoneNumber;
    private LocalDate birthday;
}
