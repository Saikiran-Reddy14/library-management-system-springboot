package com.practice.library_management.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.practice.library_management.dto.ApiResponse;
import com.practice.library_management.dto.BookReq;
import com.practice.library_management.dto.BookRes;
import com.practice.library_management.dto.BookUpdateReq;
import com.practice.library_management.dto.PaginationRes;
import com.practice.library_management.service.BookService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<BookRes>> addBook(@RequestBody @Valid BookReq request) {
        BookRes res = bookService.addBook(request);

        return ResponseEntity.status(201)
                .body(ApiResponse.<BookRes>builder()
                        .message("Book added successfully")
                        .status(201)
                        .data(res)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<PaginationRes<BookRes>>> getAllBooks(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "bookId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        PaginationRes<BookRes> res = bookService.getAllBooks(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(ApiResponse.<PaginationRes<BookRes>>builder().message("Retrieved books successfully")
                .status(200).data(res).timestamp(LocalDateTime.now()).build());
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<ApiResponse<BookRes>> getBookById(@PathVariable Long bookId) {

        BookRes res = bookService.getBookById(bookId);
        return ResponseEntity.ok(ApiResponse.<BookRes>builder().message("Retrieved book successfully")
                .status(200).data(res).timestamp(LocalDateTime.now()).build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{bookId}/update")
    public ResponseEntity<ApiResponse<BookRes>> updateBook(@PathVariable Long bookId,
            @RequestBody BookUpdateReq request) {
        BookRes res = bookService.updateBook(bookId, request);
        return ResponseEntity.ok(ApiResponse.<BookRes>builder().message("Updated book successfully")
                .status(200).data(res).timestamp(LocalDateTime.now()).build());
    }

}
