package com.example.bookclub.errors;

/**
 * 스터디 시작시간이 종료시간보다 늦는 경우 예외
 */
public class StudyStartAndEndTimeNotValidException extends RuntimeException {
    public StudyStartAndEndTimeNotValidException() {
        super("Study StartTime and EndTime not valid");
    }
}
