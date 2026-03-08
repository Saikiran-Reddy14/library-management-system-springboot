package com.practice.library_management.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.practice.library_management.dto.ApiResponse;
import com.practice.library_management.dto.BorrowReq;
import com.practice.library_management.dto.BorrowRes;
import com.practice.library_management.dto.PaginationRes;
import com.practice.library_management.exception.CustomException;
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

        @GetMapping("/history")
        public ResponseEntity<ApiResponse<List<BorrowRes>>> getBorrowHistory(
                        @AuthenticationPrincipal CustomUserDetails userDetails) {
                String email = userDetails.getUsername();
                List<BorrowRes> history = borrowService.getBorrowHistory(email);
                return ResponseEntity.ok().body(ApiResponse.<List<BorrowRes>>builder()
                                .message("Borrow History Retrieved Successfully")
                                .status(HttpStatus.OK.value())
                                .data(history)
                                .timestamp(LocalDateTime.now())
                                .build());
        }

        @GetMapping("/active")
        public ResponseEntity<ApiResponse<List<BorrowRes>>> getActiveBorrows(
                        @AuthenticationPrincipal CustomUserDetails userDetails) {
                String email = userDetails.getUsername();
                List<BorrowRes> activeBorrows = borrowService.getActiveBorrows(email);
                return ResponseEntity.ok().body(ApiResponse.<List<BorrowRes>>builder()
                                .message("Active Borrows Retrieved Successfully")
                                .status(HttpStatus.OK.value())
                                .data(activeBorrows)
                                .timestamp(LocalDateTime.now())
                                .build());
        }

        @GetMapping("/status/{status}")
        public ResponseEntity<ApiResponse<List<BorrowRes>>> getBorrowsByStatus(
                        @PathVariable String status,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {
                String upperStatus = status.toUpperCase();
                if (!upperStatus.equals("BORROWED") && !upperStatus.equals("RETURNED")
                                && !upperStatus.equals("OVERDUE")) {
                        throw new CustomException("Invalid status. Must be BORROWED, RETURNED, or OVERDUE.");
                }
                String email = userDetails.getUsername();
                List<BorrowRes> borrows = borrowService.getBorrowsByStatus(email, status);
                return ResponseEntity.ok().body(ApiResponse.<List<BorrowRes>>builder()
                                .message("Borrows with status " + status + " Retrieved Successfully")
                                .status(HttpStatus.OK.value())
                                .data(borrows)
                                .timestamp(LocalDateTime.now())
                                .build());
        }

        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/admin/status/{status}")
        public ResponseEntity<ApiResponse<PaginationRes<BorrowRes>>> getBorrowsByStatusAdmin(
                        @PathVariable String status,
                        @RequestParam(defaultValue = "0") int pageNumber,
                        @RequestParam(defaultValue = "10") int pageSize,
                        @RequestParam(defaultValue = "recordId") String sortBy,
                        @RequestParam(defaultValue = "desc") String sortOrder) {
                String borrowStatus = status.toUpperCase();
                if (!borrowStatus.equals("BORROWED") && !borrowStatus.equals("RETURNED")
                                && !borrowStatus.equals("OVERDUE")) {
                        throw new CustomException("Invalid status. Must be BORROWED, RETURNED, or OVERDUE.");
                }
                PaginationRes<BorrowRes> borrows = borrowService.getBorrowsByStatusAdmin(borrowStatus, pageNumber,
                                pageSize, sortBy, sortOrder);
                return ResponseEntity.ok().body(ApiResponse.<PaginationRes<BorrowRes>>builder()
                                .message("Borrows with status " + status + " Retrieved Successfully")
                                .status(HttpStatus.OK.value())
                                .data(borrows)
                                .timestamp(LocalDateTime.now())
                                .build());
        }

}
