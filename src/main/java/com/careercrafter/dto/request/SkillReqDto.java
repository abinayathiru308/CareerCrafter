package com.careercrafter.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SkillReqDto(

        @NotBlank(message = "Skill name is mandatory")
        String name

) {
}