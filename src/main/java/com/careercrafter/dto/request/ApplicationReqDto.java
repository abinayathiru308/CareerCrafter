package com.careercrafter.dto.request;
public record ApplicationReqDto(
        long jobListingId,
        String batch,
        String course,
        String certifications,
        String college,
        int yearPassedOut,
        String skills
) {
}