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
import javax.persistence.OneToOne;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"study", "uploadFile"})
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

    @OneToOne
    private UploadFile uploadFile;

    @Builder.Default
    private boolean deleted = false;

    @ManyToOne
    private Study study;

    @Builder
    public Account(Long id, String name, String email, String nickname,
                   String password, UploadFile uploadFile, boolean deleted, Study study) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.uploadFile = uploadFile;
        this.deleted = deleted;
        this.study = study;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account)o;
        return account.id.equals(this.id);
    }

    public void delete() {
        this.deleted = true;
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

    public void cancelStudy() {
        this.study = null;
    }

    public void addUploadFile(UploadFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
