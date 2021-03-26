package com.example.bookclub.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Day {
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

    public static List<String> getAllDays() {
        return Arrays.stream(values())
                .map(Day -> Day.day)
                .collect(Collectors.toList());
    }

    public String getDay() {
        return this.day;
    }
}
