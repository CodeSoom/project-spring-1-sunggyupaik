package com.example.bookclub.common.exception.account.emailauthentication;

/**
 * 주어진 이메일에 해당하는 인증번호가 없는 경우 예외
 */
public class EmailNotAuthenticatedException extends RuntimeException {
    public EmailNotAuthenticatedException(String email) {
        super("Email is not authenticated: " + email);
    }
}
