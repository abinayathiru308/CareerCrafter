package com.careercrafter.dto.response;

public record TokenDto(String token, String expiration, String role) {}