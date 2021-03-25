package com.example.bookclub.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
public class UserUpdateDto {
    private String nickname;

    private String password;

    private String profileImage;

    @Builder
    public UserUpdateDto(String nickname, String password, String profileImage) {
        this.nickname = nickname;
        this.password = password;
        this.profileImage = profileImage;
    }
}
