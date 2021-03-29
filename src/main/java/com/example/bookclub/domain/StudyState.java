package com.example.bookclub.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum StudyState implements EnumMapperType {
    OPEN("모집중"),
    CLOSE("마감"),
    End("완료");

    private String studyState;

    StudyState(String studyState) {
        this.studyState = studyState;
    }

    public static List<EnumMapperValue> getAllStudyStates() {
        return Arrays.stream(StudyState.values())
                .map(EnumMapperValue::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getTitle() {
        return this.studyState;
    }

    public static String getTitleFrom(Object object) {
        StudyState studyState = (StudyState) object;
        return Arrays.stream(StudyState.values())
                .filter(v -> studyState.studyState.equals(v.getTitle()))
                .findFirst()
                .map(StudyState::getTitle)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                String.format("%s는 스터디 상태 형식에 맞지 않습니다.", object.toString())
                        ));
    }
}
