package com.example.authservice.exception;

import com.example.authservice.auth.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionController {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionController.class);

    private void logPrint(String message, HttpServletRequest request) {
        logger.error("error message: {} at path : {}",message, request.getRequestURI());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse> usernameNotFoundHandler(UsernameNotFoundException e, HttpServletRequest request) {

        logPrint(e.getMessage(),request);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.failure(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> generalExceptionHandler(Exception e, HttpServletRequest request) {

        logPrint(e.getMessage(),request);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.failure(e.getMessage()));
    }
}
