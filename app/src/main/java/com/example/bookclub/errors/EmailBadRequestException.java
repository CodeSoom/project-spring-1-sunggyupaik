package com.example.bookclub.errors;

public class EmailBadRequestException extends RuntimeException {
    public EmailBadRequestException(String email) {
        super("Email bad request: " + email);
    }
}
