package com.example.bookclub.domain.study;

import com.example.bookclub.common.EnumMapperType;
import com.example.bookclub.common.EnumMapperValue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum StudyState implements EnumMapperType {
    OPEN("모집중"),
    CLOSE("진행중"),
    END("종료");

    private String studyState;

    StudyState(String studyState) {
        this.studyState = studyState;
    }

    public static List<EnumMapperValue> getAllStudyStates() {
        return Arrays.stream(StudyState.values())
                .map(EnumMapperValue::new)
                .collect(Collectors.toList());
    }

    public static List<EnumMapperValue> getAllStudyStatesSelectedWith(StudyState studyState) {
        return Arrays.stream(StudyState.values())
                .map(enumMapperType -> {
                    if(enumMapperType.getCode().equals(studyState.toString())) {
                        return new EnumMapperValue(enumMapperType, true);
                    }
                    return new EnumMapperValue(enumMapperType);
                })
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

    public static StudyState getStudyState(StudyState studyState) {
        return Arrays.stream(StudyState.values())
                .filter(d -> d.getCode().equals(studyState.getCode()))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                String.format("%s는 스터디 상태 형식에 맞지 않습니다.", studyState.toString())
                        )
                );
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
