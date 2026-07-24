package com.careercrafter.config;

import com.careercrafter.service.MyUserSecurityService;
import com.careercrafter.utility.JwtUtility;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtility jwtUtility;
    private final MyUserSecurityService myUserSecurityService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        System.out.println("=== JwtFilter running for: " + request.getRequestURI());
        System.out.println("Authorization header: " + authorizationHeader);

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtility.extractUsername(jwt);
                System.out.println("Extracted username: " + username);
            } catch (Exception e) {
                System.out.println("ERROR extracting username: " + e.getMessage());
            }
        } else {
            System.out.println("No Bearer token found in header");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("Proceeding to load user and validate...");
            UserDetails userDetails = myUserSecurityService.loadUserByUsername(username);

            boolean isValid = jwtUtility.validateToken(jwt, userDetails.getUsername());
            System.out.println("Token valid? " + isValid);

            if (isValid) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                System.out.println("Authentication SET successfully");
            }
        } else {
            System.out.println("Skipped auth block - username null or already authenticated");
        }

        filterChain.doFilter(request, response);
    }
}