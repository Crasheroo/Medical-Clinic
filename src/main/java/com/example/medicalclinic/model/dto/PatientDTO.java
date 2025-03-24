package com.example.medicalclinic.model.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDTO {
    private Long id;
    private String email;
    private String idCardNo;
    private String fullName;
    private String phoneNumber;
    private LocalDate birthday;
}
