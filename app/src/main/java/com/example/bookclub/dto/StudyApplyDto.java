package com.example.bookclub.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyApplyDto {
    String email;

    @Builder
    public StudyApplyDto(String email) {
        this.email = email;
    }
}
