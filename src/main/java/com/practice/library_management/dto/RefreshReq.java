package com.practice.library_management.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshReq(
        @NotBlank(message = "Refresh token is required") String refreshToken) {

}
