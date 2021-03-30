package com.example.bookclub.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionCreateDto {
    private String email;

    private String password;

    @Builder
    public SessionCreateDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
