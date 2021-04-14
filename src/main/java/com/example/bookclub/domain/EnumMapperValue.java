package com.example.bookclub.domain;

public class EnumMapperValue {
    private String code;
    private String title;

    public EnumMapperValue(EnumMapperType enumMapperType) {
        this.code = enumMapperType.getCode();
        this.title = enumMapperType.getTitle();
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public String
    toString() {
        return "{" +
                "code='" + code + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
