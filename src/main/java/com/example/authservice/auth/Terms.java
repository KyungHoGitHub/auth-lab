package com.example.authservice.auth;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class Terms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String key;

    String title;

    String description;

    boolean required;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;
}
