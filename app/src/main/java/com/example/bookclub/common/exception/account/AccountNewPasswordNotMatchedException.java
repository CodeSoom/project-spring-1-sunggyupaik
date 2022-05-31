package com.example.bookclub.common.exception.account;

/**
 * 비밀번호와 비밀번호 확인이 일치하지 않는 경우 예외
 */
public class AccountNewPasswordNotMatchedException extends RuntimeException {
    public AccountNewPasswordNotMatchedException() {
        super("NewPassword not matched");
    }
}
