package com.careercrafter.dto.response;

public record ApplicationRespDto(

        Long applicationId,
        String status,
        String jobTitle,
        String employerName,
        String jobSeekerName,
        String resumeUrl,
        String batch,
        String course,
        String certifications,
        String college,
        Integer yearPassedOut,
        String skills

) {
}