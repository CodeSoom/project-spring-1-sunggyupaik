package com.example.bookclub.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Builder
public class AccountUpdateDto {
    @Size(min=3, max=10)
    private String nickname;

    private String savedFileName;

    @Builder.Default
    private String password = "";

    @Builder
    public AccountUpdateDto(String nickname, String savedFileName, String password) {
        this.nickname = nickname;
        this.savedFileName = savedFileName;
        this.password = password;
    }
}
