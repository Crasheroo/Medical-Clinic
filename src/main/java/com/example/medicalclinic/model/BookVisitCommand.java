package com.example.medicalclinic.model;

import lombok.Builder;

@Builder
public record BookVisitCommand(
        Long visitId,
        Long patientId
) {

}
