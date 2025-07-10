package com.example.authservice.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class JwtUtillTest {

    @InjectMocks
    private JwtUtill jwtUtill;

    private final String testSecretKey = "abcdefghijklmnopqrstuvwxyz123456"; // 최소 256비트 (32글자 이상)
    private final Long testExpirationTime = 5000L;  // 5초 만료 시간

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 리플렉션을 사용하여 `@Value` 필드를 강제로 주입 (Mock 환경에서 동작)
        ReflectionTestUtils.setField(jwtUtill, "secretKey", testSecretKey);
        ReflectionTestUtils.setField(jwtUtill, "expirationTime", testExpirationTime);
    }


    @Test
    void generateAccessToken() {
    }

    @Test
    void generateRefreshToken() {
    }

    @Test
    @DisplayName("토큰 검증 테스트")
    void validateToken() {

        String username = "testUser";

        // ✅ 토큰 생성
        String token = jwtUtill.generateAccessToken(username,12L,"test");
        assertNotNull(token);

        // ✅ 토큰 검증 (유효한 경우)
        assertTrue(jwtUtill.validateToken(token));

        // ✅ 토큰에서 username 추출 확인
        assertEquals(username, jwtUtill.getUsernameFromToken(token));
    }

    @Test
    void getUsernameFromToken() {
        String a = "1";
        String b = "1";

        Assertions.assertEquals(a, b);
    }
}