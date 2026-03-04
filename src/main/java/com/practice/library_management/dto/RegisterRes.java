package com.practice.library_management.dto;

import lombok.Builder;

@Builder
public record RegisterRes(
        String username,
        String email) {

}
