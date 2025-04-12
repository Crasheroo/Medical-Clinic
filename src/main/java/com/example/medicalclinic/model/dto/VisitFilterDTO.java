package com.example.medicalclinic.model.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitFilterDTO {
    private Long doctorId;
    private String speciality;
    private LocalDate date;
    private boolean onlyAvailable;
    private String patientEmail;
}
