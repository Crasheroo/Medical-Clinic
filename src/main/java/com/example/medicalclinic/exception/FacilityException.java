package com.example.medicalclinic.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FacilityException extends RuntimeException {
    private final HttpStatus status;

    public FacilityException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
