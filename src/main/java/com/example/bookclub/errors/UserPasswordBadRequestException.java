package com.example.bookclub.errors;

public class UserPasswordBadRequestException extends RuntimeException {
    public UserPasswordBadRequestException() {
        super("Password bad request");
    }
}
