package com.example.bookclub.errors;

public class UserEmailDuplicatedException extends RuntimeException {
    public UserEmailDuplicatedException(String email) {
        super("Email is already existed: " + email);
    }
}
