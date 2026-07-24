package com.careercrafter.dto.response;

public record ApplicationRespDto(

        Long applicationId,
        String status,
        String jobTitle,
        String employerName,
        String jobSeekerName

) {
}