package com.careercrafter.mapper;

import com.careercrafter.dto.response.NotificationRespDto;
import com.careercrafter.model.Notification;

public class NotificationMapper {

    public static NotificationRespDto convertEntityToDto(Notification notification) {

        return new NotificationRespDto(

                notification.getId(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedOn()

        );
    }

}