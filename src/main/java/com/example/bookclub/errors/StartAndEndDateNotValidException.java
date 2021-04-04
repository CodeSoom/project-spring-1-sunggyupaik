package com.example.bookclub.errors;

public class StartAndEndDateNotValidException extends RuntimeException {
    public StartAndEndDateNotValidException() {
        super("StartDate and EndDate not valid");
    }
}
