package com.example.authservice.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUserId(String userId);

    boolean existsByUserId(String userId);

    Optional<User> findByUserIdOrUsername(String userId, String username);

    Optional<User> findByEmail(String email);
}
