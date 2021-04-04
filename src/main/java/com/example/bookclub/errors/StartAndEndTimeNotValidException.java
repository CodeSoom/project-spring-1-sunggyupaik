package com.example.bookclub.errors;

public class StartAndEndTimeNotValidException extends RuntimeException {
    public StartAndEndTimeNotValidException() {
        super("StartTime and EndTime not valid");
    }
}
