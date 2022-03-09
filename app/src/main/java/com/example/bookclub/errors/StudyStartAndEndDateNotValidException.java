package com.example.bookclub.errors;

/**
 * 스터디 시작날짜가 종료날짜보다 늦는 경우 예외
 */
public class StudyStartAndEndDateNotValidException extends RuntimeException {
    public StudyStartAndEndDateNotValidException() {
        super("Study StartDate and EndDate not valid");
    }
}
