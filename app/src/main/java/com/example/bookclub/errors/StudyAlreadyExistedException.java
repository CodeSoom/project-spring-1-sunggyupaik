package com.example.bookclub.errors;

public class StudyAlreadyExistedException extends RuntimeException {
    public StudyAlreadyExistedException() {
        super("Study already existed");
    }
}

