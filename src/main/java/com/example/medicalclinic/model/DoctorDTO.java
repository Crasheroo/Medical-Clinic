package com.example.medicalclinic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorDTO {
    private Long id;
    private String email;
    private List<String> facilityNames;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoctorDTO doctorDTO)) return false;
        return Objects.equals(id, doctorDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}