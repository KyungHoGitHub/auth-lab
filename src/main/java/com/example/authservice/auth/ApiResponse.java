package com.example.authservice.auth;


import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private T data;
    private String message;


    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(data, message);
    }

    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>(null, message);
    }
}
