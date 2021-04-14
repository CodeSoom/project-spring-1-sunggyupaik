package com.example.bookclub.errors;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long id) {
        super("User not found: " + id);
    }
}
