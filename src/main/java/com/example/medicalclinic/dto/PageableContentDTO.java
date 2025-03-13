package com.example.medicalclinic.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public record PageableContentDTO<T>(
        int totalPages,
        long totalElements,
        int currentPage,
        List<T> content
) {
    public static <T> PageableContentDTO <T> from(Page<T> page, List<T> content) {
        return new PageableContentDTO<>(
                page.getTotalPages(),
                page.getTotalElements(),
                page.getNumber(),
                content
        );
    }
}