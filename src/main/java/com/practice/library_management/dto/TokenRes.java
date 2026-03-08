package com.practice.library_management.dto;

import lombok.Builder;

@Builder
public record TokenRes(
                String accessToken,
                String refreshToken,
                String role) {

}
