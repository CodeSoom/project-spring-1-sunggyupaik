package com.example.bookclub.common.exception.study;

/**
 * 스터디가 이미 존재하는 경우 예외
 */
public class StudyAlreadyExistedException extends RuntimeException {
    public StudyAlreadyExistedException() {
        super("Study already existed");
    }
}

