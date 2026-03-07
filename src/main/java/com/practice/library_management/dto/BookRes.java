package com.practice.library_management.dto;

import lombok.Builder;

@Builder
public record BookRes(
        Long bookId,
        String title,
        String isbn,
        Integer totalCopies,
        Integer availableCopies,
        String authorName,
        String categoryName) {

}
