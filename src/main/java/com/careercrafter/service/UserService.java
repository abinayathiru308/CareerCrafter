package com.careercrafter.service;

import com.careercrafter.exception.InvalidCredentialsException;
import com.careercrafter.model.User;
import com.careercrafter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserDetails(String username) {
        return userRepository.loadUserByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Login Denied"));
    }
}