package com.example.bookclub.errors;

public class AccountPasswordBadRequestException extends RuntimeException {
    public AccountPasswordBadRequestException() {
        super("Password bad request");
    }
}
