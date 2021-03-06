package com.example.bookclub.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class SessionResultDto {
    private String accessToken;

    @Builder
    public SessionResultDto(String accessToken) {
        this.accessToken = accessToken;
    }

    public static SessionResultDto of(String accessToken) {
        return SessionResultDto.builder()
                .accessToken(accessToken)
                .build();
    }
}
