package com.example.bookclub.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Day implements EnumMapperType {
    MONDAY("월요일"),
    TUESDAY("화요일"),
    WEDNESDAY("수요일"),
    THURSDAY("목요일"),
    FRIDAY("금요일"),
    SATURDAY("토요일"),
    SUNDAY("일요일");

    private String day;

    Day(String day) {
        this.day = day;
    }

    public static List<EnumMapperValue> getAllDays() {
        return Arrays.stream(Day.values())
                .map(EnumMapperValue::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getTitle() {
        return this.day;
    }
}
