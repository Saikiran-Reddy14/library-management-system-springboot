package com.practice.library_management.dto;

import lombok.Builder;

@Builder
public record LoginRes(
        String accessToken,
        String refreshToken) {

}
