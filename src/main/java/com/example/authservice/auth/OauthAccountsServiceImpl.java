package com.example.authservice.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OauthAccountsServiceImpl implements OauthAccountsService {
    private final OauthAccountsRepository oauthAccountsRepository;


    @Override
    public void createOauthAccounts(OauthAccounts oauthAccounts) {
        oauthAccountsRepository.save(oauthAccounts);
    }

    @Override
    public void linkCreateOauthAccounts(String providerUserId) {
        OauthAccounts.builder()
                .provider("google")
                .providerUserId(providerUserId)
                .build();
    }

    public boolean existOauthAccountByProviderUserId(String googleId) {
        return oauthAccountsRepository.existsByProviderUserId(googleId);
    }
}
