package com.example.bookclub.errors;

public class AccountNewPasswordNotMatchedException extends RuntimeException {
    public AccountNewPasswordNotMatchedException() {
        super("NewPassword not matched");
    }
}
