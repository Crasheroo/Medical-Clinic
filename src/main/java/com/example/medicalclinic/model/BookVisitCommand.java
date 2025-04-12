package com.example.medicalclinic.model;

public record BookVisitCommand(
        Long visitId,
        Long patientid
) {

}
