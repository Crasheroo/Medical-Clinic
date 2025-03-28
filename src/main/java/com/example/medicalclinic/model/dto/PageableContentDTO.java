package com.example.medicalclinic.model.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageableContentDTO<T>(
        int totalPages,
        long totalElements,
        int currentPage,
        List<T> content
) {
    public static <T, R> PageableContentDTO<R> from(Page<T> page, List<R> content) {
        return new PageableContentDTO<>(
                page.getTotalPages(),
                page.getTotalElements(),
                page.getNumber(),
                content
        );
    }
}