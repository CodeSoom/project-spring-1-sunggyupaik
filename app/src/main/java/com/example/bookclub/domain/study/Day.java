package com.example.bookclub.domain.study;

import com.example.bookclub.common.EnumMapperType;
import com.example.bookclub.common.EnumMapperValue;

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

    public static Day getDay(Day day) {
        return Arrays.stream(Day.values())
                .filter(d -> d.getCode().equals(day.getCode()))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                String.format("%s는 요일 형식에 맞지 않습니다.", day.toString())
                        )
                );
    }

    public static List<EnumMapperValue> getAllDaysSelectedWith(Day day) {
        return Arrays.stream(Day.values())
                .map(enumMapperType -> {
                    if(enumMapperType.getCode().equals(day.toString())) {
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
        return this.day;
    }

    public static String getTitleFrom(Object object) {
        Day dayCode = (Day) object;
        return Arrays.stream(Day.values())
                .filter(v -> dayCode.day.equals(v.day))
                .findFirst()
                .map(Day::getTitle)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                String.format("%s는 요일 형식에 맞지 않습니다.", object.toString())
                        ));
    }
}
