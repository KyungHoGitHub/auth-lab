package com.example.authservice.auth;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "oauth_accounts")
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OauthAccounts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "user_idx", foreignKey = @ForeignKey(name = "fk_oauth_user"))
    User user;

    String provider;

    @Column(name = "provider_user_id")
    String providerUserId;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @CreationTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
