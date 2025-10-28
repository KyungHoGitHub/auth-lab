package com.example.authservice.auth;

public interface UserTermsRegistrationService {
    GoogleUserCache getUserFromRedis(String email);

    User registerTermsAgreementsUser(String userEmail);
}
