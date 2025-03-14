package com.example.medicalclinic.dto;

import lombok.*;

import java.util.List;
import java.util.Objects;

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