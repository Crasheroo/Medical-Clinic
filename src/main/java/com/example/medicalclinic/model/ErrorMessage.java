package com.example.medicalclinic.model;

import lombok.Builder;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Builder
public record ErrorMessage
        (String message,
         HttpStatus status,
         LocalDateTime errorTime
        ) {
}