package com.practice.library_management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookReq(
                @NotBlank(message = "Title cannot be empty") String title,
                @NotBlank(message = "Isbn cannot be empty") String isbn,
                @NotNull(message = "Total copies cannot be null") @Min(value = 1, message = "Total copies must be at least 1") Integer totalCopies,
                @NotBlank(message = "Author name cannot be empty") String authorName,
                @NotBlank(message = "Category name cannot be empty") String categoryName) {

}
