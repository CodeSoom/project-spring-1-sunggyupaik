package com.example.bookclub.Dto;

import com.example.bookclub.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class UserResultDto {
    private Long id;

    private String name;

    private String email;

    private String nickname;

    private String password;

    private String profileImage;

    private boolean deleted;

    @Builder
    public UserResultDto(Long id, String name, String email, String nickname,
                         String password, String profileImage, boolean deleted) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.profileImage = profileImage;
        this.deleted = deleted;
    }

    public static UserResultDto of(User user) {
        return UserResultDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .password(user.getPassword())
                .profileImage(user.getProfileImage())
                .deleted(user.isDeleted())
                .build();
    }
}
