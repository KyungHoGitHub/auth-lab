package com.example.authservice.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleUserInfo {
    private String googleId;
    private String email;
    private String name;
    private String picture;
    private Boolean emailVerified;
}
