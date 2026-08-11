package com.careercrafter.dto.response;

import java.time.Instant;

public record NotificationRespDto(

        Long id,
        String message,
        Boolean isRead,
        Instant createdOn

) {
}