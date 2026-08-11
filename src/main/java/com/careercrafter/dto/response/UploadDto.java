package com.careercrafter.dto.response;

public record UploadDto(
        long jobSeekerId,
        String path,
        String fileName,
        String message
) {
}