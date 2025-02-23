package com.example.medicalclinic.handler;

import com.example.medicalclinic.exception.PatientException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class MedicalClinicExceptionHandler extends ResponseEntityExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PatientException.class)
    public ErrorMessage handlePatientException(PatientException ex) {
        return new ErrorMessage(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
