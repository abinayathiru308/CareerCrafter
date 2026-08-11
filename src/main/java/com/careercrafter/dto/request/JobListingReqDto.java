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
        // Convenience constructor for tests with default values
        public JobListingReqDto(String title, String description) {
                this(title, description, 0.0, "Remote", 1L);
        }

        // Convenience constructor without location/salary
        public JobListingReqDto(String title, String description, long categoryId) {
                this(title, description, 0.0, "Remote", categoryId);
        }
}