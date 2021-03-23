package com.example.bookclub.application;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    public String getUser(Long id) {
        return "\"id\":1L,\"name\":\"홍길동\",\"email\":\"abcd@naver.com\",\"nickname\":\"abcd\",\"password\":\"1234\",\"profileImage\":\"image\"";
    }
}
