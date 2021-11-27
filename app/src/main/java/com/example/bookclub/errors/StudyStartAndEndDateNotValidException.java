package com.example.bookclub.errors;

public class StudyStartAndEndDateNotValidException extends RuntimeException {
    public StudyStartAndEndDateNotValidException() {
        super("Study StartDate and EndDate not valid");
    }
}
