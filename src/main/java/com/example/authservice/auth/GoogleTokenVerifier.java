package com.example.authservice.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleTokenVerifier {
    @Value("${google.client-id}")
    private String clientId;

    public GoogleUserInfo verifyAccessTokenAndGetUserInfo(String accessToken) {
        try {
            // Google UserInfo API 호출
            String url = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> userInfo = response.getBody();

                return GoogleUserInfo.builder()
                        .googleId((String) userInfo.get("id"))
                        .email((String) userInfo.get("email"))
                        .name((String) userInfo.get("name"))
                        .picture((String) userInfo.get("picture"))
                        .emailVerified((Boolean) userInfo.get("verified_email"))
                        .build();
            } else {
                throw new RuntimeException("Failed to get user info from Google");
            }

        } catch (Exception e) {
            throw new RuntimeException("Google access token verification failed: " + e.getMessage(), e);
        }
    }

    public GoogleUserInfo verifyTokenAndGetUserInfo(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory())
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                return GoogleUserInfo.builder()
                        .googleId(payload.getSubject())
                        .email(payload.getEmail())
                        .name((String) payload.get("name"))
                        .picture((String) payload.get("picture"))
                        .emailVerified(payload.getEmailVerified())
                        .build();
            } else {
                log.warn("Invalid Google ID token received");
                throw new RuntimeException("유효하지 않은 Google 토큰입니다.");
            }

        } catch (Exception e) {
            log.error("Google token verification failed: {}", e.getMessage());
            throw new RuntimeException("Google 토큰 검증에 실패했습니다: " + e.getMessage(), e);
        }
    }
}
