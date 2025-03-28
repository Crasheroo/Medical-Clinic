package com.example.medicalclinic.model.entity;

import com.example.medicalclinic.model.CreateDoctorCommand;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DOCTOR")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "doctor_facility",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id")
    )
    private Set<Facility> facilities = new HashSet<>();

    public void updateFrom(String newEmail, String newPassword) {
        Optional.ofNullable(newEmail)
                .filter(email -> !email.equals(this.email))
                .ifPresent(email -> this.email = email);

        Optional.ofNullable(newPassword)
                .ifPresent(password -> this.password = password);
    }

    public static Doctor from(CreateDoctorCommand request) {
        return Doctor.builder()
                .email(request.email())
                .password(request.password())
                .facilities(new HashSet<>())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doctor))
            return false;
        Doctor other = (Doctor) o;
        return id != null &&
                id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}