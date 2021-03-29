package com.example.bookclub.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Zone implements EnumMapperType {
    SEOUL("서울특별시"), GYEONGGI("경기도"), INCHEON("인천광역시"), GANGWON("강원도"),
    CHUNGCHEONGNAMDO("충청남도"), DAEJON("대전광역시"), CHUNGCHEONGBUKDO("충청북도"), SEJONG("세종특별자치시"),
    BUSAN("부산광역시"), ULSAN("울산광역시"), DAEGU("대구광역시"), GYEONGSANKBUKDO("경상북도"),
    GYEONGSANKNAMDO("경상남도"), JEOLLANAMDO("전라남도"), GWANGJU("광주광역시"), JEOLLABUKDO("전라북도"), JEJU("제주특별자치도");

    private String zone;

    Zone(String zone) {
        this.zone = zone;
    }

    public static List<EnumMapperValue> getAllZones() {
        return Arrays.stream(Zone.values())
                .map(EnumMapperValue::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getTitle() {
        return this.zone;
    }

    public static String getTitleFrom(Object object) {
        Zone zone = (Zone) object;
        return Arrays.stream(Zone.values())
                .filter(v -> zone.zone.equals(v.getTitle()))
                .findFirst()
                .map(Zone::getTitle)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                String.format("%s는 지역 형식에 맞지 않습니다.", object.toString())
                        ));
    }
}
