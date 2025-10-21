package com.example.authservice.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoogleUserCache implements Serializable {

     static final long serialVersionUID = 1L;

     String googleId;

     String email;

     String picture;

     boolean emailVerified;

}
