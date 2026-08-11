package com.careercrafter.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminReqDto(
        @NotBlank(message = "Username is mandatory")
        String username,

        @NotBlank(message = "Password is mandatory")
        @Size(min = 5, max = 15, message = "Password should have 5-15 chars")
        String password
) {
}