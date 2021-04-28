package com.example.bookclub.errors;

public class NewPasswordNotMatchedException extends RuntimeException {
    public NewPasswordNotMatchedException() {
        super("NewPassword not matched");
    }
}
