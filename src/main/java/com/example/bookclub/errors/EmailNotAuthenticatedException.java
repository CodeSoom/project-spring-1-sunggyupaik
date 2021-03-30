package com.example.bookclub.errors;

public class EmailNotAuthenticatedException extends RuntimeException {
    public EmailNotAuthenticatedException(String email) {
        super("Email is not authenticated: " + email);
    }
}
