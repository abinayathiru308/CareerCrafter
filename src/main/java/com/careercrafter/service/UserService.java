package com.careercrafter.service;

import com.careercrafter.dto.request.AdminReqDto;
import com.careercrafter.dto.response.UserResponseDto;
import com.careercrafter.enums.Role;
import com.careercrafter.exception.InvalidCredentialsException;
import com.careercrafter.exception.ResourceNotFoundException;
import com.careercrafter.model.User;
import com.careercrafter.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void addAdmin(AdminReqDto dto) {

        boolean usernameExists =
                userRepository.existsByUsername(dto.username());

        if (usernameExists) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();

        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(Role.ADMIN);
        user.setActivated(true);

        userRepository.save(user);
    }

    public User getUserDetails(String username) {

        return userRepository.loadUserByUsername(username)
                .orElseThrow(() ->
                        new InvalidCredentialsException("Login Denied"));
    }

    public List<UserResponseDto> searchUsers(
            String keyword,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return userRepository.searchUsers(keyword, pageable)
                .stream()
                .map(user -> new UserResponseDto(
                        user.getId(),
                        user.getUsername(),
                        user.getRole().toString(),
                        user.isActivated()
                ))
                .toList();
    }

    public void activateUser(long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User id invalid"));

        user.setActivated(true);

        userRepository.save(user);
    }

    public void deactivateUser(long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User id invalid"));

        user.setActivated(false);

        userRepository.save(user);
    }

    public void deleteUser(long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User id invalid"));

        userRepository.delete(user);
    }

    public List<UserResponseDto> getAllUsers(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return userRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(user -> new UserResponseDto(
                        user.getId(),
                        user.getUsername(),
                        user.getRole().toString(),
                        user.isActivated()
                ))
                .toList();
    }
}