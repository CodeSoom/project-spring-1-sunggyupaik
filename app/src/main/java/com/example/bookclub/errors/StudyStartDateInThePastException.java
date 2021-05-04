package com.example.bookclub.errors;

public class StudyStartDateInThePastException extends RuntimeException {
    public StudyStartDateInThePastException() {
        super("Study startDate in the past");
    }
}
