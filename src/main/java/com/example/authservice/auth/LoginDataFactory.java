package com.example.authservice.auth;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LoginDataFactory {

    public Object createLoginData(AuthRequestDto requestDto) {
        LoginType loginType = requestDto.getLoginType();
        Map<String, String> data = requestDto.getData();

        switch (loginType) {
            case ID_PASSWORD:
                if (!data.containsKey("userId") || !data.containsKey("password")) {
                    throw new IllegalArgumentException("Missing required fields for ID_PASSWORD login");
                }
                IdPasswordLoginData idPasswordData = new IdPasswordLoginData();
                idPasswordData.setUserId(data.get("userId"));
                idPasswordData.setPassword(data.get("password"));
                return idPasswordData;
            case GOOGLE:
                if (!data.containsKey("token")) {
                    throw new IllegalArgumentException("Missing token for GOOGLE login");
                }

                GoogleOauthTokenRequest googleData = new GoogleOauthTokenRequest(data.get("token"));
                return googleData;
            default:
                throw new IllegalArgumentException("Unknown login type: " + loginType);
        }
    }
}
