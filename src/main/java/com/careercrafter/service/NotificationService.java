package com.careercrafter.service;

import com.careercrafter.dto.response.NotificationRespDto;
import com.careercrafter.exception.ResourceNotFoundException;
import com.careercrafter.mapper.NotificationMapper;
import com.careercrafter.model.Notification;
import com.careercrafter.model.User;
import com.careercrafter.repository.NotificationRepository;
import com.careercrafter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void sendNotification(String username, String message) {

        User user = userRepository.loadUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User invalid"));

        Notification notification = new Notification();

        notification.setMessage(message);
        notification.setUser(user);

        notificationRepository.save(notification);
    }

    public List<NotificationRespDto> getByUsername(String username) {

        List<Notification> list = notificationRepository.findByUser_Username(username);

        return list
                .stream()
                .map(NotificationMapper::convertEntityToDto)
                .toList();
    }

    public void markAsRead(long id) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification id invalid"));

        notification.setRead(true);

        notificationRepository.save(notification);
    }

}
