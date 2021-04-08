package com.example.bookclub.errors;

public class AccountEmailDuplicatedException extends RuntimeException {
    public AccountEmailDuplicatedException(String email) {
        super("Email is already existed: " + email);
    }
}
