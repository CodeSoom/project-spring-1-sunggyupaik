package com.example.bookclub.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum StudyState {
    OPEN("모집중"),
    CLOSE("마감"),
    End("완료");

    String studyState;

    StudyState(String studyState) {
        this.studyState = studyState;
    }

    public static List<String> getAllStudyStates () {
        return Arrays.stream(values())
                .map(StudyState -> StudyState.studyState)
                .collect(Collectors.toList());
    }

    public String getStudyState() {
        return this.studyState = studyState;
    }

}
