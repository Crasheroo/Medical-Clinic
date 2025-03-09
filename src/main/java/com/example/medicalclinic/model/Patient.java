package com.example.medicalclinic.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PATIENT")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    @Column(unique = true)
    private String idCardNo;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthday;

    public void updateFrom(Patient other) {
        if (other.getPassword() != null) {
            this.password = other.getPassword();
        }
        if (other.getFirstName() != null) {
            this.firstName = other.getFirstName();
        }
        if (other.getLastName() != null) {
            this.lastName = other.getLastName();
        }
        if (other.getBirthday() != null) {
            this.birthday = other.getBirthday();
        }
        if (other.getEmail() != null) {
            this.email = other.getEmail();
        }
        if (other.getPhoneNumber() != null) {
            this.phoneNumber = other.getPhoneNumber();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient patient)) return false;
        return Objects.equals(id, patient.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
