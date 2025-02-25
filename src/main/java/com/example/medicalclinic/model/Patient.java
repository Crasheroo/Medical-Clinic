package com.example.medicalclinic.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class Patient {
    private String email;
    private String password;
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
        if (other.getIdCardNo() != null) {
            this.idCardNo = other.getIdCardNo();
        }
        if (other.getEmail() != null) {
            this.email = other.getEmail();
        }
        if (other.getPhoneNumber() != null) {
            this.phoneNumber = other.getPhoneNumber();
        }
    }
}
