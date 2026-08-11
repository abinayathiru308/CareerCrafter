package com.careercrafter.controller;

import com.careercrafter.dto.request.AdminReqDto;
import com.careercrafter.dto.response.TokenDto;
import com.careercrafter.model.User;
import com.careercrafter.service.UserService;
import com.careercrafter.utility.JwtUtility;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import com.careercrafter.dto.response.UserResponseDto;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserService userService;
    private final JwtUtility jwtUtility;
    private Logger logger = LoggerFactory.getLogger("AuthController.class");

    @PostMapping("/add/admin")
    public String addAdmin(@Valid @RequestBody AdminReqDto adminReqDto) {
        userService.addAdmin(adminReqDto);
        return "Admin registration successful!";
    }

    @GetMapping("/login")
    public TokenDto login(Principal principal) {
        String loggedInUsername = principal.getName();
        logger.info("Logged In Username {}", loggedInUsername);

        String token = jwtUtility.generateToken(loggedInUsername);
        logger.info("Token Generated {}", token);

        User user = userService.getUserDetails(loggedInUsername);

        logger.info("User Details fetched from DB having role: {}", user.getRole());
        logger.info("Token Expiry {}", jwtUtility.extractExpiration(token).toString());

        return new TokenDto(
                token,
                jwtUtility.extractExpiration(token).toString(),
                user.getRole().toString()
        );


    }

    @GetMapping("/user-details")
    public UserResponseDto getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserDetails(userDetails.getUsername());
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getRole().toString(),
                user.isActivated()   // or whatever the actual getter is called
        );
    }
    @GetMapping("/users")
    public List<UserResponseDto> getAllUsers(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return userService.getAllUsers(page, size);
    }
}