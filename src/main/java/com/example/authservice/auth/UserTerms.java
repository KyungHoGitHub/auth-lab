package com.example.authservice.auth;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTerms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_idx")
    Long userIdx;

    @Column(name = "term_id")
    Long termId;

    boolean agreed;

    @CreationTimestamp
    @Column(name = "agreed_at")
    LocalDateTime agreedAt;
}
