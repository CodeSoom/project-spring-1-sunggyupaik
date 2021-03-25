package com.example.bookclub.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class EmailAuthentication {
    @Id
    @GeneratedValue
    private Long id;

    private String email;

    private String authenticationNumber;

    @Builder
    public EmailAuthentication(String email, String authenticationNumber) {
        this.email = email;
        this.authenticationNumber = authenticationNumber;
    }
}
