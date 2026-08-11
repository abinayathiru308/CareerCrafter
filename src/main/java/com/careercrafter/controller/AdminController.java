package com.careercrafter.controller;

import com.careercrafter.dto.response.UserResponseDto;
import com.careercrafter.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public List<UserResponseDto> getAllUsers(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return userService.getAllUsers(page, size);
    }

    @GetMapping("/users/search")
    public List<UserResponseDto> searchUsers(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return userService.searchUsers(keyword, page, size);
    }
    @PutMapping("/users/{id}/activate")
    public void activateUser(@PathVariable long id) {
        userService.activateUser(id);
    }

    @PutMapping("/users/{id}/deactivate")
    public void deactivateUser(@PathVariable long id) {
        userService.deactivateUser(id);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }
}