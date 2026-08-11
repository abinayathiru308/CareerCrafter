package com.careercrafter.controller;

import com.careercrafter.dto.response.NotificationRespDto;
import com.careercrafter.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/by-user")
    public List<NotificationRespDto> getByUsername(@RequestParam String username) {
        return notificationService.getByUsername(username);
    }

    @PutMapping("/mark-read/{id}")
    public void markAsRead(@PathVariable long id) {
        notificationService.markAsRead(id);
    }

}