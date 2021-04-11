package com.example.bookclub.dto;

import com.example.bookclub.domain.UploadFile;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
public class AccountUpdateDto {
    @Size(min=3, max=10)
    private String nickname;

    @Size(min = 4, max = 1024)
    private String password;

    @Size(min = 4, max = 1024)
    private String newPassword;

    private UploadFile uploadFile;

    @Builder
    public AccountUpdateDto(String nickname, String password,
                            String newPassword, UploadFile uploadFile) {
        this.nickname = nickname;
        this.password = password;
        this.newPassword = newPassword;
        this.uploadFile = uploadFile;
    }
}
