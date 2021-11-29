package com.example.bookclub.errors;

public class StudyStartAndEndTimeNotValidException extends RuntimeException {
    public StudyStartAndEndTimeNotValidException() {
        super("Study StartTime and EndTime not valid");
    }
}
