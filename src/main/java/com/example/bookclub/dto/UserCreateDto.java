package com.example.bookclub.dto;

import com.example.bookclub.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class UserCreateDto {
    @Size(min=2, max=6)
    private String name;

    @Size(min=3, max=48)
    private String email;

    @Size(min=3, max=10)
    private String nickname;

    @Size(min = 4, max = 1024)
    private String password;

    private String profileImage;

    @Size(min = 4, max = 5)
    private String authenticationNumber;

    @Builder
    public UserCreateDto(String name, String email, String nickname,
                         String password, String profileImage) {
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.profileImage = profileImage;
    }

    public User toEntity() {
        return User.builder()
                .name(this.name)
                .email(this.email)
                .nickname(this.nickname)
                .password(this.password)
                .profileImage(this.profileImage)
                .deleted(false)
                .build();
    }
}
