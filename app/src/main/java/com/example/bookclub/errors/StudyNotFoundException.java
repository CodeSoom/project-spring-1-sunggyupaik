package com.example.bookclub.errors;

public class StudyNotFoundException extends RuntimeException {
    public StudyNotFoundException(Long id) {
        super("Study not found : " + id);
    }
}
