package com.example.authservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtUtill {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expirationTime;
    private final long clockSkewSeconds = 30; // 클럭 스큐 30초 허용

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpirationTime;

    private final JwtProperties jwtProperties;

    // JWT 엑세스 토큰 생성
    public String generateAccessToken(String username, Long userIdx, String userId,String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        // 클레임 ( userName, useIdx, userId ) 값 세팅
        Claims claims = Jwts.claims()
                .setSubject(username);
        claims.put("userIdx", userIdx);
        claims.put("userId", userId);
        claims.put("role", role);

        return Jwts.builder()
                .setSubject(username) // 인증 대상 - ex) username, userId
                .setIssuer(jwtProperties.getIssuer()) // 발급자
                .setIssuedAt(now)
                .setClaims(claims)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationTime);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public String generateToken(String username, TokenType tokenType) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationTime);

        if (tokenType == TokenType.ACCESS_TOKEN) {

        }
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }


    // JWT 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8))).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;  // 토큰이 잘못되었거나 만료된 경우
        } catch (Exception e) {
            return false;
        }
    }

    // refresh 토큰 검증후  access  토큰 재발급
    public String reissueAccessToken(String refreshToken) {
        // refresh 토큰 유효 한지 분기
        if (validateToken(refreshToken)) {
            // 유효하면 엑세스 토큰 발급

            // 유저 이름이 필요한데 그건 리프레쉬 토큰에서
            String userName = getUsernameFromToken(refreshToken);

            return generateAccessToken(refreshToken, 1L,"test","test");

        }
        throw new IllegalArgumentException();
    }


    // JWT에서 사용자 정보 추출
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰 정보 파싱
    public Claims getClaimsFromToken(String token) throws JwtException {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8))) // 서명 키 설정
                    .build()
                    .parseClaimsJws(token) // 토큰 파싱
                    .getBody(); // claims 반환
        } catch (ExpiredJwtException e) {
            throw e;
        }
    }
}
