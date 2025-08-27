package com.example.authservice.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenPolicyServiceImpl implements TokenPolicyService {
    private final TokenPolicyRepository tokenPolicyRepository;
    private final KafkaTemplate<String, TokenPolicyEvent> kafkaTemplate;

    @Override
    public String createTokenPolicy(TokenPolicyRequestDTO requestDTO) {
        TokenPolicy tokenPolicy = new TokenPolicy();
        tokenPolicy.setAccessTokenExp(requestDTO.getAccessTokenExp());
        tokenPolicy.setRefreshTokenExp(requestDTO.getRefreshTokenExp());

        tokenPolicyRepository.save(tokenPolicy);
        
        return "success";
    }
}
