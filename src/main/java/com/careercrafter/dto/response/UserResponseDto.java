package com.careercrafter.dto.response;

public record UserResponseDto(
        long id,
        String username,
        String role,
        boolean isActivated
) {
}