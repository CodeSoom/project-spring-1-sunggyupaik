package com.example.bookclub.dto;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.UploadFile;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class AccountResultDto {
    private Long id;

    private String name;

    private String email;

    private String nickname;

    private String password;

    private UploadFile uploadFile;

    private boolean deleted;

    @Builder
    public AccountResultDto(Long id, String name, String email, String nickname,
                            String password, UploadFile uploadFile, boolean deleted) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.uploadFile = uploadFile;
        this.deleted = deleted;
    }

    public static AccountResultDto of(Account account) {
        return AccountResultDto.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .nickname(account.getNickname())
                .password(account.getPassword())
                .uploadFile(account.getUploadFile())
                .deleted(account.isDeleted())
                .build();
    }
}
