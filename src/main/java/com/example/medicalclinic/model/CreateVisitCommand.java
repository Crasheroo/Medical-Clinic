package com.example.medicalclinic.model;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateVisitCommand(
        Long doctorId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
