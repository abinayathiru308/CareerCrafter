package com.careercrafter.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record EmployerReqDto(
        @NotBlank(message = "Company name is mandatory")
        String companyName,
        @NotBlank(message = "City is mandatory")
        String city,
        @NotBlank(message = "Username is mandatory")
        String username,
        @NotBlank(message = "password is mandatory")
        @Size(min = 5, max = 15, message = "Password should've min 5 and max 15 chars")
        String password
) {
}