package com.example.bookclub.domain;

public class EnumMapperValue {
    private String code;
    private String title;
    private boolean selected;

    public EnumMapperValue(EnumMapperType enumMapperType) {
        this.code = enumMapperType.getCode();
        this.title = enumMapperType.getTitle();
        this.selected = false;
    }

    public EnumMapperValue(EnumMapperType enumMapperType, boolean selected) {
        this.code = enumMapperType.getCode();
        this.title = enumMapperType.getTitle();
        this.selected = selected;
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public String toString() {
        return "EnumMapperValue{" +
                "code='" + code + '\'' +
                ", title='" + title + '\'' +
                ", selected=" + selected +
                '}';
    }
}
