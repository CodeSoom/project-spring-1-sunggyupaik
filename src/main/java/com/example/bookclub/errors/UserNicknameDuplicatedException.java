package com.example.bookclub.errors;

public class UserNicknameDuplicatedException extends RuntimeException {
    public UserNicknameDuplicatedException(String nickname) {
        super("Nickname is already Existed: " + nickname);
    }
}
