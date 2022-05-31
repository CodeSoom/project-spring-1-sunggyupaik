package com.example.bookclub.common.exception.account;

/**
 * 주어진 닉네임에 해당하는 사용자가 존재하는 경우
 */
public class AccountNicknameDuplicatedException extends RuntimeException {
    public AccountNicknameDuplicatedException(String nickname) {
        super("Nickname is already existed: " + nickname);
    }
}
