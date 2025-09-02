package com.example.authservice.auth;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoogleLoginRequestDTO {
    String token;
    LoginType loginType;
}
