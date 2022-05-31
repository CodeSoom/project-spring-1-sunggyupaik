package com.example.bookclub.domain.study;

import com.example.bookclub.common.EnumMapperType;

import java.util.Arrays;

public enum BookType implements EnumMapperType {
    BESTSELLER("베스트셀러"), RECOMMEND("추천도서"), NEW("신간도서"), SEARCH("검색도서");

    private String bookType;

    BookType(String bookType) {
        this.bookType = bookType;
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getTitle() {
        return this.bookType;
    }

    public static String getTitleFrom(Object object) {
        BookType bookTypeCode = (BookType) object;
        return Arrays.stream(BookType.values())
                .filter(v -> bookTypeCode.bookType.equals(v.bookType))
                .findFirst()
                .map(BookType::getTitle)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                String.format("%s는 도서 종류 형식에 맞지 않습니다.", object.toString())
                        ));
    }
}
