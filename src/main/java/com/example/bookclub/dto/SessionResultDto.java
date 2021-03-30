package com.example.bookclub.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SessionResultDto {
    private String accessToken;

    @Builder
    public SessionResultDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
