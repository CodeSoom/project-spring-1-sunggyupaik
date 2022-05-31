package com.example.bookclub.errors.account.emailauthentication;

/**
 * 메일 전송 요청이 잘못된 경우
 */
public class EmailBadRequestException extends RuntimeException {
    public EmailBadRequestException(String email) {
        super("Email bad request: " + email);
    }
}
