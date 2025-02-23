package com.example.medicalclinic.handler;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ErrorMessage {
    private final String message;
    private final HttpStatus status;
    private final LocalDateTime errorTime;

    public ErrorMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
        this.errorTime = LocalDateTime.now();
    }
}
