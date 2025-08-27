package com.example.authservice.auth;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idx;

    @Column(name = "access_token_exp")
    Long accessTokenExp;

    @Column(name = "refresh_token_exp")
    Long refreshTokenExp;

    String role;

    @CreationTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
