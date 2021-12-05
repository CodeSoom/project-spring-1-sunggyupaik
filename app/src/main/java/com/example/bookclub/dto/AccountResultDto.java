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

    private boolean deleted;

    private UploadFileResultDto uploadFileResultDto;

    private StudyResultDto studyResultDto;

    @Builder
    public AccountResultDto(Long id, String name, String email, String nickname, String password,
                            boolean deleted, UploadFileResultDto uploadFileResultDto, StudyResultDto studyResultDto) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.deleted = deleted;
        this.uploadFileResultDto = uploadFileResultDto;
        this.studyResultDto = studyResultDto;
    }

    public static AccountResultDto of(Account account) {
        return AccountResultDto.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .nickname(account.getNickname())
                .password(account.getPassword())
                .deleted(account.isDeleted())
                .uploadFileResultDto(UploadFileResultDto.of(account.getUploadFile()))
                .studyResultDto(StudyResultDto.of(account.getStudy()))
                .build();
    }
}
