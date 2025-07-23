package com.example.authservice.auth;

import java.time.LocalDateTime;

public record UserResponseDto(
        Long idx,
        String userId,
        String userName,
        String role,
        LocalDateTime createdAt,
        String email
) {
    public static UserResponseDto toDto(User user) {
        return new UserResponseDto(
                user.getIdx(),
                user.getUserId(),
                user.getUsername(),
                user.getRole(),
                user.getCreatedAt(),
                user.getEmail()

        );
    }
}
