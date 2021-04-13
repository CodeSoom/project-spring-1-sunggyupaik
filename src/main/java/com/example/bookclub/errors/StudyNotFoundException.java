package com.example.bookclub.errors;

public class StudyNotFoundException extends RuntimeException {
    public StudyNotFoundException() {
        super("Study not found:");
    }
}
