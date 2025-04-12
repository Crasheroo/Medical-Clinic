package com.example.medicalclinic.model.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitFilterDTO {
    private Long doctorId;
    private String speciality;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;
    private boolean onlyAvailable;
    private String patientEmail;
}
