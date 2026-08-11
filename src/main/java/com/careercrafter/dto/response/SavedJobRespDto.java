package com.careercrafter.dto.response;

import java.time.Instant;

public record SavedJobRespDto(

        Long id,
        String jobTitle,
        String companyName,
        Instant savedOn

) {
}