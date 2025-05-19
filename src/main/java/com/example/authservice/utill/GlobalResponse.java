package com.example.authservice.utill;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
public class GlobalResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;


    public GlobalResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // 기본 성공 처리
    public static <T> ResponseEntity<GlobalResponse<T>> success(T data) {
        return ResponseEntity.ok(new GlobalResponse<>(true, "요청성공", data));
    }

    // 기본 실패 처리
    public static <T> ResponseEntity<GlobalResponse<T>> fail(String message) {
        return ResponseEntity.badRequest().body(new GlobalResponse<>(false,message,null));
    }

    // 상세한 상태 처리
    public static  <T> ResponseEntity<GlobalResponse<T>> of (HttpStatus status, boolean success, String message, T data) {
        return ResponseEntity.status(status).body(new GlobalResponse<>(success,message,data));
    }
}
