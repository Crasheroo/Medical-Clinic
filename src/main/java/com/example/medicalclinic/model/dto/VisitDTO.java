package com.example.medicalclinic.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class VisitDTO {
    private Long id;
    private DoctorDTO doctor;
    private PatientDTO patient;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isAvailable;
}
