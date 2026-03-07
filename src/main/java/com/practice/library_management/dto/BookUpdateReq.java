package com.practice.library_management.dto;

public record BookUpdateReq(
        String title,
        String isbn,
        Long totalCopies,
        String authorName,
        String categoryName) {

}
