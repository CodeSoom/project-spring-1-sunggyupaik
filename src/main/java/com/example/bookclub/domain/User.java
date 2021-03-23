package com.example.bookclub.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class User {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String email;

    private String nickname;

    private String password;

    private String profileImage;

    private boolean deleted;

    @Builder
    public User(Long id, String name, String email, String nickname, String password, String profileImage) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.profileImage = profileImage;
        this.deleted = isDeleted();
    }
}
