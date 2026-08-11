package com.careercrafter.service;

import com.careercrafter.dto.response.NotificationRespDto;
import com.careercrafter.enums.Role;
import com.careercrafter.exception.ResourceNotFoundException;
import com.careercrafter.model.Notification;
import com.careercrafter.model.User;
import com.careercrafter.repository.NotificationRepository;
import com.careercrafter.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;

    private User user1;
    private Notification notification1;

    @BeforeEach
    public void init(){

        user1 = new User(1L, "seeker1", "pass123", Role.JOBSEEKER, true);
        notification1 = new Notification(1L, "Your application status changed", false, null, user1);
    }

    @Test
    public void sendNotificationTest(){

        when(userRepository.loadUserByUsername("seeker1")).thenReturn(Optional.of(user1));

        notificationService.sendNotification("seeker1", "Test message");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(1)).save(captor.capture());

        Assertions.assertEquals("Test message", captor.getValue().getMessage());
    }

    @Test
    public void sendNotificationTestForInvalidUser(){

        when(userRepository.loadUserByUsername("ghost")).thenReturn(Optional.empty());

        Assertions.assertEquals("User invalid",
                Assertions.assertThrows(ResourceNotFoundException.class,
                                () -> notificationService.sendNotification("ghost", "Test message"))
                        .getMessage());

        verify(notificationRepository, times(0)).save(any(Notification.class));
    }

    @Test
    public void getByUsernameTest(){

        when(notificationRepository.findByUser_Username("seeker1")).thenReturn(List.of(notification1));

        List<NotificationRespDto> result = notificationService.getByUsername("seeker1");

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Your application status changed", result.get(0).message());
    }

    @Test
    public void markAsReadTest(){

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification1));

        notificationService.markAsRead(1L);

        Assertions.assertTrue(notification1.isRead());
        verify(notificationRepository, times(1)).save(notification1);
    }

    @Test
    public void markAsReadTestForInvalidId(){

        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertEquals("Notification id invalid",
                Assertions.assertThrows(ResourceNotFoundException.class,
                                () -> notificationService.markAsRead(99L))
                        .getMessage());

        verify(notificationRepository, times(0)).save(any(Notification.class));
    }
}