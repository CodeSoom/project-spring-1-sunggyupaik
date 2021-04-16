package com.example.bookclub.errors;

public class StudyAlreadyStartedException extends RuntimeException {
    public StudyAlreadyStartedException() {
        super("Study already started");
    }
}
