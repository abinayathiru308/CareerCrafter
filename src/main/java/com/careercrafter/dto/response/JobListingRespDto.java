package com.careercrafter.dto.response;

public record JobListingRespDto(
        long id,
        String title,
        double salary,
        String location,
        String categoryName,
        String employerName
) {}