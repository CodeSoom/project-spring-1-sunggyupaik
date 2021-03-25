package com.example.bookclub.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
public class UserUpdateDto {
    @Size(min=3, max=10)
    private String nickname;

    @Size(min = 4, max = 1024)
    private String password;

    @Size(min = 4, max = 1024)
    private String newPassword;

    private String profileImage;

    @Builder
    public UserUpdateDto(String nickname, String password,
                         String newPassword, String profileImage) {
        this.nickname = nickname;
        this.password = password;
        this.newPassword = newPassword;
        this.profileImage = profileImage;
    }
}
