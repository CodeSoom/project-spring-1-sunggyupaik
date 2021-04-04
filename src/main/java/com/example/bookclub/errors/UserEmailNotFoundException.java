package com.example.bookclub.errors;

public class UserEmailNotFoundException extends RuntimeException {
    public UserEmailNotFoundException(String email) {
        super("User email not found: " + email);
    }
}
