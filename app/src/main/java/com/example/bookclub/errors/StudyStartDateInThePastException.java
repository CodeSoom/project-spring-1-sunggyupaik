package com.example.bookclub.errors;

/**
 * 스터디 시작시간이 과거인 경우 예외
 */
public class StudyStartDateInThePastException extends RuntimeException {
    public StudyStartDateInThePastException() {
        super("Study startDate in the past");
    }
}
