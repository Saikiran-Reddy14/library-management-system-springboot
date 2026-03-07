package com.practice.library_management.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record PaginationRes<T>(
        List<T> data,
        int pageNumber,
        int pageSize,
        long totalElements,
        long totalPages,
        boolean hasNext) {

}
