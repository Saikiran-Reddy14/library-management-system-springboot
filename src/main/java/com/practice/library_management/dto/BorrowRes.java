package com.practice.library_management.dto;

import java.time.LocalDate;

import com.practice.library_management.entity.BorrowStatus;

import lombok.Builder;

@Builder
public record BorrowRes(
        Long recordId,
        String bookTitle,
        String username,
        LocalDate borrowDate,
        LocalDate dueDate,
        LocalDate returnDate,
        BorrowStatus borrowStatus) {

}
