package com.example.bookclub.common.exception.account;

/**
 * 주어진 사용자 식별자에 해당하는 사용자가 없는 경우
 */
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long id) {
        super("User not found: " + id);
    }
}
