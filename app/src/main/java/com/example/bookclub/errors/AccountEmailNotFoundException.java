package com.example.bookclub.errors;

/**
 * 주어진 이메일에 해당하는 사용자가 없을 경우 예외
 */
public class AccountEmailNotFoundException extends RuntimeException {
    public AccountEmailNotFoundException(String email) {
        super("User email not found: " + email);
    }
}
