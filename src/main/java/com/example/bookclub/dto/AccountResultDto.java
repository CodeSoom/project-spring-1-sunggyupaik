package com.example.bookclub.dto;

import com.example.bookclub.domain.Account;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class AccountResultDto {
    private Long id;

    private String name;

    private String email;

    private String nickname;

    private String password;

    private String profileImage;

    private boolean deleted;

    @Builder
    public AccountResultDto(Long id, String name, String email, String nickname,
                            String password, String profileImage, boolean deleted) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.profileImage = profileImage;
        this.deleted = deleted;
    }

    public static AccountResultDto of(Account account) {
        return AccountResultDto.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .nickname(account.getNickname())
                .password(account.getPassword())
                .profileImage(account.getProfileImage())
                .deleted(account.isDeleted())
                .build();
    }
}