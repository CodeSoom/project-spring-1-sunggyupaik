package com.example.bookclub.errors;

public class AccountEmailNotFoundException extends RuntimeException {
    public AccountEmailNotFoundException(String email) {
        super("User email not found: " + email);
    }
}
