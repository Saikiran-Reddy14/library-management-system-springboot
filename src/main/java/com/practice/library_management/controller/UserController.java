package com.practice.library_management.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practice.library_management.dto.ApiResponse;
import com.practice.library_management.dto.LoginReq;
import com.practice.library_management.dto.RefreshReq;
import com.practice.library_management.dto.TokenRes;
import com.practice.library_management.dto.RegisterReq;
import com.practice.library_management.dto.RegisterRes;
import com.practice.library_management.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterRes>> register(@RequestBody @Valid RegisterReq request) {
        RegisterRes res = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<RegisterRes>builder()
                        .message("User registered successfully")
                        .status(HttpStatus.CREATED.value())
                        .data(res)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenRes>> login(@RequestBody @Valid LoginReq request) {
        TokenRes res = userService.login(request);
        return ResponseEntity
                .ok(ApiResponse.<TokenRes>builder()
                        .message("Login successful")
                        .status(HttpStatus.OK.value())
                        .data(res)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) throws Exception {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Void>builder()
                            .message("Invalid Authorization header")
                            .status(HttpStatus.BAD_REQUEST.value())
                            .timestamp(LocalDateTime.now())
                            .build());
        }
        String token = authHeader.substring(7);

        userService.logout(token);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("User logged out successfully")
                .status(HttpStatus.OK.value())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRes>> refresh(@RequestBody @Valid RefreshReq request) {
        TokenRes res = userService.refresh(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.<TokenRes>builder()
                .message("Token refreshed successfully")
                .status(HttpStatus.OK.value())
                .data(res)
                .timestamp(LocalDateTime.now())
                .build());
    }

}
