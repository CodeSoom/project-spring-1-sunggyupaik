package com.example.bookclub.errors.account;

/**
 * 사용자 비밀번호 요청이 잘못된 경우
 */
public class AccountPasswordBadRequestException extends RuntimeException {
    public AccountPasswordBadRequestException() {
        super("Password bad request");
    }
}
