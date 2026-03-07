package com.practice.library_management.dto;

import lombok.Builder;

@Builder
public record BookRes(
                Long bookId,
                String title,
                String isbn,
                Long totalCopies,
                Long availableCopies,
                String authorName,
                String categoryName) {

}
