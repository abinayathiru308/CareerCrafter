package com.careercrafter.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record JobSeekerDto(
        @NotBlank(message = "Name cannot be blank")
        @Size(min = 3, message = "Name should be minimum 3 chars")
        String name,
        @NotBlank(message = "Email is mandatory")
        String email,
        String skills
) {
}