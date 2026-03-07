package com.practice.library_management.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practice.library_management.dto.ApiResponse;
import com.practice.library_management.dto.BorrowReq;
import com.practice.library_management.dto.BorrowRes;
import com.practice.library_management.security.CustomUserDetails;
import com.practice.library_management.service.BorrowService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/borrow")
public class BorrowController {

    private final BorrowService borrowService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<BorrowRes>> borrowBook(@RequestBody BorrowReq request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getUsername();
        BorrowRes borrowRes = borrowService.borrowBook(request, email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<BorrowRes>builder()
                        .message("Book Borrowed Successfully")
                        .status(HttpStatus.CREATED.value())
                        .data(borrowRes)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @PostMapping("/{recordId}/return")
    public ResponseEntity<ApiResponse<BorrowRes>> returnBook(@PathVariable Long recordId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getUsername();
        BorrowRes borrowRes = borrowService.returnBook(recordId, email);
        return ResponseEntity.ok().body(ApiResponse.<BorrowRes>builder()
                .message("Book Returned Successfully")
                .status(HttpStatus.OK.value())
                .data(borrowRes)
                .timestamp(LocalDateTime.now())
                .build());
    }

}
