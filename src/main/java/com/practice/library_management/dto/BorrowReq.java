package com.practice.library_management.dto;

import jakarta.validation.constraints.NotNull;

public record BorrowReq(
        @NotNull(message = "Id cannot be empty") Long bookId) {
}