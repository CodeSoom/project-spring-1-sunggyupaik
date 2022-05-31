package com.example.bookclub.common.exception.account;

/**
 * 사용자 인증 요청이 잘못된 경우
 */
public class AuthenticationBadRequestException extends RuntimeException {
    public AuthenticationBadRequestException() {
        super("Authentication Bad Request");
    }
}
