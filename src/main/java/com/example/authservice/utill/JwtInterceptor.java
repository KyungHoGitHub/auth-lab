package com.example.authservice.utill;

import com.example.authservice.auth.User;
import com.example.authservice.config.JwtUtill;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtill jwtUtill;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // 회원가입, 로그인 등의 경로는 인증 제외
        if (path.startsWith("/auth/sign/up") || path.startsWith("/auth/login")) {
            return true;
        }


        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 제거
            Claims claims = jwtUtill.getClaimsFromToken(token); // JWT 디코딩

            String userId = claims.get("userId", String.class);
            String username = claims.get("sub", String.class);

            UserContext.setUser(User.builder().username(username).build()); // ThreadLocal에 저장
        }

        return true;
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.removeUser(); // 요청 완료 후 ThreadLocal 정리
    }
}
