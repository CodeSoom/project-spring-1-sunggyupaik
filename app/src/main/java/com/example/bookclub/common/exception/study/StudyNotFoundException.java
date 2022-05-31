package com.example.bookclub.common.exception.study;

/**
 * 주어진 스터디 식별자에 해당하는 스터디가 없는 경우 예외
 */
public class StudyNotFoundException extends RuntimeException {
    public StudyNotFoundException(Long id) {
        super("Study not found : " + id);
    }
}
