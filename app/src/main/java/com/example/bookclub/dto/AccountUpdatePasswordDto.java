package com.example.bookclub.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Builder
public class AccountUpdatePasswordDto {
    private String password;

    @Size(min=4, max=20)
    private String newPassword;

    @Size(min=4, max=20)
    private String newPasswordConfirmed;

    @Builder
    public AccountUpdatePasswordDto(String password, String newPassword, String newPasswordConfirmed) {
        this.password = password;
        this.newPassword = newPassword;
        this.newPasswordConfirmed = newPasswordConfirmed;
    }
}
