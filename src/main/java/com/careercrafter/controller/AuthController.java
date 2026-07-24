package com.careercrafter.controller;

import com.careercrafter.dto.response.TokenDto;
import com.careercrafter.model.User;
import com.careercrafter.service.UserService;
import com.careercrafter.utility.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtility jwtUtility;

    // Before the user hits this /login api here in controller, spring would have already checked credentials
    @GetMapping("/login")
    public TokenDto login(Principal principal) {
        String loggedInUsername = principal.getName();

        // Generate the token for this username
        String token = jwtUtility.generateToken(loggedInUsername);

        // fetch user details to pass the role
        User user = userService.getUserDetails(loggedInUsername);

        return new TokenDto(
                token,
                jwtUtility.extractExpiration(token).toString(),
                user.getRole().toString()
        );
    }
}