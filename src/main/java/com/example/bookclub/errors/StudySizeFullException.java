package com.example.bookclub.errors;

public class StudySizeFullException extends RuntimeException {
    public StudySizeFullException() {
        super("Study size is full");
    }
}
