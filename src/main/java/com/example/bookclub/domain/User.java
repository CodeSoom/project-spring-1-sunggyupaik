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
@Builder
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Builder.Default
    private String name = "";

    @Builder.Default
    private String email = "";

    @Builder.Default
    private String nickname = "";

    @Builder.Default
    private String password = "";

    @Builder.Default
    private String profileImage = "";

    @Builder.Default
    private boolean deleted = false;

    @Builder
    public User(Long id, String name, String email, String nickname,
                String password, String profileImage, boolean deleted) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.profileImage = profileImage;
        this.deleted = deleted;
    }

    public void delete() {
        this.deleted = true;
    }
}
