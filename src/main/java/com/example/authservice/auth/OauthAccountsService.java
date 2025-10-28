package com.example.authservice.auth;

public interface OauthAccountsService {
    void createOauthAccounts(OauthAccounts oauthAccounts);
    void linkCreateOauthAccounts(String providerUserId);
    boolean existOauthAccountByProviderUserId(String googleId);
}
