package com.example.bookclub.dto;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.UploadFile;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class AccountCreateDto {
    @Size(min=2, max=6)
    private String name;

    @Size(min=3, max=48)
    private String email;

    @Size(min=3, max=10)
    private String nickname;

    @Size(min = 4, max = 1024)
    private String password;

    private UploadFile uploadFile;
    
    private String authenticationNumber;

    @Builder
    public AccountCreateDto(String name, String email, String nickname, String password,
                            UploadFile uploadFile, String authenticationNumber) {
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.uploadFile = uploadFile;
        this.authenticationNumber = authenticationNumber;
    }

    public Account toEntity() {
        return Account.builder()
                .name(this.name)
                .email(this.email)
                .nickname(this.nickname)
                .password(this.password)
                .uploadFile(this.uploadFile)
                .deleted(false)
                .build();
    }
}
