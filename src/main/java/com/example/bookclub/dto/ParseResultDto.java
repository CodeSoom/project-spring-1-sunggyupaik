package com.example.bookclub.dto;

import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParseResultDto {
    private Claims claims;

    @Builder
    public ParseResultDto(Claims claims) {
        this.claims = claims;
    }
}
