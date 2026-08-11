package com.careercrafter.dto.response;

public record JobSeekerProfileRespDto(
        long id,
        String name,
        String email,
        String phone,
        String skills
) {
}