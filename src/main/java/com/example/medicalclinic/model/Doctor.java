package com.example.medicalclinic.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    @ManyToMany(mappedBy = "doctors")
    @JsonIgnore
    private List<Facility> facilities;

    public void updateFrom(Doctor other) {
        if (other.getPassword() != null) {
            this.password = other.getPassword();
        }
        if (other.getEmail() != null) {
            this.email = other.getEmail();
        }
    }
}