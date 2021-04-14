package com.example.bookclub.errors;

public class AccountNicknameDuplicatedException extends RuntimeException {
    public AccountNicknameDuplicatedException(String nickname) {
        super("Nickname is already Existed: " + nickname);
    }
}
