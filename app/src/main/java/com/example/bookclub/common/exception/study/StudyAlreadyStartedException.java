package com.example.bookclub.common.exception.study;

/**
 * 스터디가 이미 시작한 경우 예외
 */
public class StudyAlreadyStartedException extends RuntimeException {
    public StudyAlreadyStartedException() {
        super("Study already started");
    }
}
