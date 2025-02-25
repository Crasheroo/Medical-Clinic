package com.example.medicalclinic.handler;

import com.example.medicalclinic.exception.PatientException;
import com.example.medicalclinic.model.ErrorMessage;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class MedicalClinicExceptionHandler extends ResponseEntityExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PatientException.class)
    public ErrorMessage handlePatientException(PatientException ex) {
        return ErrorMessage.builder()
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .errorTime(LocalDateTime.now())
                .build();
    }
}
