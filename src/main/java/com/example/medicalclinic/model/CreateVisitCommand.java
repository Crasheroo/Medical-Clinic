package com.example.medicalclinic.model;

import java.time.LocalDateTime;

public record CreateVisitCommand(
        Long doctorId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
