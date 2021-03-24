package com.example.bookclub.Dto;

import com.example.bookclub.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class UserCreateDto {
    @NotBlank(message = "name 값은 필수입니다")
    @Size(min=2)
    private String name;

    @NotBlank(message = "email 값은 필수입니다")
    @Size(min=3)
    private String email;

    @NotBlank(message = "nickname 값은 필수입니다")
    @Size(min=3)
    private String nickname;

    @NotBlank(message = "password 값은 필수입니다")
    @Size(min = 4, max = 1024)
    private String password;

    private String profileImage;

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
