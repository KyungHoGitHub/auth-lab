package com.example.authservice.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OauthAccountsRepository extends JpaRepository<OauthAccounts, Long> {
    Optional<OauthAccounts> findByProviderUserId(String providerUserId);

    boolean existsByProviderUserId(String providerUserId);
}
