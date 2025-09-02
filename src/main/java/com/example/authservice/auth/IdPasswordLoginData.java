package com.example.authservice.auth;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PACKAGE)
public class IdPasswordLoginData {
    String userId;

    String password;
}
