package com.example.bookclub.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class EmailAuthentication {
    @Id @GeneratedValue
    @Column(name = "EMAILAUTHENTICATION_ID")
    private Long id;

    private String email;

    private String authenticationNumber;

    @Builder
    public EmailAuthentication(Long id, String email, String authenticationNumber) {
        this.id = id;
        this.email = email;
        this.authenticationNumber = authenticationNumber;
    }

    @Builder
    public EmailAuthentication(String email, String authenticationNumber) {
        this.email = email;
        this.authenticationNumber = authenticationNumber;
    }

    public boolean isSameWith(String authenticationNumber) {
        return this.authenticationNumber.equals(authenticationNumber);
    }
}
