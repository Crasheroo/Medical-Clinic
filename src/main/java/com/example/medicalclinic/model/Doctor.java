package com.example.medicalclinic.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Getter
@Setter
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
    @ManyToMany(mappedBy = "doctors", cascade = CascadeType.REMOVE)
    private Set<Facility> facilities = new LinkedHashSet<>();

    public void updateFrom(Doctor other) {
        if (other.getPassword() != null) {
            this.password = other.getPassword();
        }
        if (other.getEmail() != null) {
            this.email = other.getEmail();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doctor doctor)) return false;
        return Objects.equals(id, doctor.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}