package com.example.bookclub.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Zone {
    A01("서울특별시"), A02("경기도"), A03("인천광역시"), A04("강원도"),
    A05("충청남도"), A06("대전광역시"), A07("충청북도"), A08("세종특별자치시"),
    A09("부산광역시"), A10("울산광역시"), A11("대구광역시"), A12("경상북도"),
    A13("경상남도"), A14("전라남도"), A15("광주광역시"), A16("전라북도"), A17("제주특별자치도");

    private String zone;

    Zone(String zone) {
        this.zone = zone;
    }

    public static List<String> getAllZones() {
        return Arrays.stream(values())
                .map(Zone -> Zone.zone)
                .collect(Collectors.toList());
    }

    public String getZone() {
        return this.zone;
    }
}
