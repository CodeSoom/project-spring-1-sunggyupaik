package com.example.bookclub.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "study")
@Builder
public class Account {
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

    @ManyToOne
    private Study study;

    @Builder
    public Account(Long id, String name, String email, String nickname,
                   String password, String profileImage, boolean deleted, Study study) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.profileImage = profileImage;
        this.deleted = deleted;
        this.study = study;
    }

    public void delete() {
        this.deleted = true;
    }

    public void updateWith(String nickname, String password, String profileImage) {
        this.nickname = nickname;
        this.password = password;
        this.profileImage = profileImage;
    }

    public boolean isPasswordSameWith(String password, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(password, this.password);
    }

    public boolean authenticate(String password, PasswordEncoder passwordEncoder) {
        return !this.deleted && passwordEncoder.matches(password, this.password);
    }

    public void updatePassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public void addStudy(Study study) {
        this.study = study;
    }
}
