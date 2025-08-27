package com.example.authservice.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenPolicyRepository extends JpaRepository<TokenPolicy, Long> {
    Optional<TokenPolicy> findByRole(String role);
}
