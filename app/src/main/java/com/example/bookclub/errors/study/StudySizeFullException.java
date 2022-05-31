package com.example.bookclub.errors.study;

/**
 * 스터디 정원이 다 찬 경우 예외
 */
public class StudySizeFullException extends RuntimeException {
    public StudySizeFullException() {
        super("Study size is full");
    }
}
