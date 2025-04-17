package com.example.medicalclinic.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PatientException extends RuntimeException {
    private final HttpStatus status;

    public PatientException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
