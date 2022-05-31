package com.example.bookclub.common.exception.account;

/**
 * 주어진 이메일에 해당하는 사용자가 중복일 경우 예외
 */
public class AccountEmailDuplicatedException extends RuntimeException {
    public AccountEmailDuplicatedException(String email) {
        super("Email is already existed: " + email);
    }
}
