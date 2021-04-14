package com.example.bookclub.errors;

public class AuthenticationBadRequestException extends RuntimeException {
    public AuthenticationBadRequestException() {
        super("Authentication Bad Request");
    }
}
