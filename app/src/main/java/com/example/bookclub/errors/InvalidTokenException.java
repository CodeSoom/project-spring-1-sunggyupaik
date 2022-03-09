package com.example.bookclub.errors;

/**
 * 주어짅 토큰이 유효하지 않는 경우 예외
 */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String token) {
        super("Invalid token: " + token);
    }
}
