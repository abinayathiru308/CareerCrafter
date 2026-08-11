package com.careercrafter.dto.request;
import jakarta.validation.constraints.NotBlank;
public record JobListingReqDto(
        @NotBlank(message = "Field is mandatory")
        String title,
        @NotBlank(message = "Field is mandatory")
        String description,
        double salary,
        String location,
        long categoryId
) {
}