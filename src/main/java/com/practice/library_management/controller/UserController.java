package com.practice.library_management.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practice.library_management.dto.ApiResponse;
import com.practice.library_management.dto.RegisterReq;
import com.practice.library_management.dto.RegisterRes;
import com.practice.library_management.service.UserService;

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
                .body(ApiResponse.<RegisterRes>builder().message("User registered successfully")
                        .status(HttpStatus.CREATED.value()).data(res).timestamp(LocalDateTime.now()).build());
    }

}
