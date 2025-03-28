package com.example.medicalclinic.model.dto;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class DoctorDTO {
    private Long id;
    private String email;
    private List<Long> facilityIds;
}