package com.example.bookclub.domain;

import com.example.bookclub.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString
public class Account extends BaseTimeEntity {
    @Id
    @Column(name = "ACCOUNT_ID")
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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private UploadFile uploadFile;

    @Builder.Default
    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Study study;

    @OneToMany(mappedBy = "account")
    @ToString.Exclude
    private List<AccountHistory> accountHistories = new ArrayList<>();

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Builder
    public Account(Long id, String name, String email, String nickname, String password, UploadFile uploadFile,
                   boolean deleted, Study study, List<AccountHistory> accountHistories) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.uploadFile = uploadFile;
        this.deleted = deleted;
        this.study = study;
        this.accountHistories = accountHistories;
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
        this.uploadFile = null;
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

    public boolean isMangerOf(Study study) {
        return this.study != null && this.email.equals(study.getEmail());
    }

    public boolean isApplierOf(Study study) {
        return this.study != null && this.study.getId().equals(study.getId())
                && !this.email.equals(study.getEmail());
    }

    public void deleteStudy() {
        this.study = null;
    }
}
