package com.example.bookclub.domain;

import com.example.bookclub.common.AccountEntityListener;
import com.example.bookclub.common.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 사용자
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
@EntityListeners(value = { AccountEntityListener.class })
@JsonIgnoreProperties({"study", "accountHistories", "studyLikes", "studyComments"})
public class Account extends BaseTimeEntity {
    /* 식별자 */
    @Id @GeneratedValue
    @Column(name = "ACCOUNT_ID")
    private Long id;

    /* 이름 */
    private String name;

    /* 이메일 */
    private String email;

    /* 닉네임 */
    private String nickname;

    /* 비밀번호 */
    private String password;

    /* 업로드파일 */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private UploadFile uploadFile;

    /* 삭제여부 */
    private boolean deleted;

    /* 스터디 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_ID")
    @ToString.Exclude
    private Study study;

    /* 사용자 히스토리 리스트 */
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<AccountHistory> accountHistories = new ArrayList<>();

    /* 이메일 */
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<StudyLike> studyLikes = new ArrayList<>();

    /* 스터디 댓글 리스트 */
    @OneToMany(mappedBy = "account")
    @ToString.Exclude
    private List<StudyComment> studyComments = new ArrayList<>();

    /* 스터디 댓글 좋아요 리스트 */
    @OneToMany(mappedBy = "account")
    @ToString.Exclude
    private List<StudyCommentLike> studyCommentLikes = new ArrayList<>();

    /* 즐겨찾기 리스트 */
    @OneToMany(mappedBy = "account")
    @ToString.Exclude
    private List<Favorite> favorites = new ArrayList<>();

    /* 즐겨찾기 갯수 */
    @Transient
    private int favoritesCount;

    @Builder
    public Account(Long id, String name, String email, String nickname, String password, UploadFile uploadFile,
                   boolean deleted, Study study, List<AccountHistory> accountHistories, List<StudyLike> studyLikes,
                   List<StudyComment> studyComments, List<StudyCommentLike> studyCommentLikes, List<Favorite> favorites,
                   int favoritesCount) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.uploadFile = uploadFile;
        this.deleted = deleted;
        this.study = study;
        this.accountHistories = accountHistories;
        this.studyLikes = studyLikes;
        this.studyComments = studyComments;
        this.studyCommentLikes = studyCommentLikes;
        this.favorites = favorites;
        this.favoritesCount = favoritesCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Account account = (Account)o;
        return account.id.equals(this.id);
    }

    public void delete() {
        this.deleted = true;
        this.uploadFile = null;
    }

    /**
     * 비밀번호 검사기로 주어진 비밀번호가 저장된 비밀번호와 일치하는지 여부를 반환한다.
     *
     * @param password 비밀번호
     * @param passwordEncoder 비밀번호 검사기
     * @return 비밀번호가 저장된 비밀번호와 일치하는지 여부
     */
    public boolean isPasswordSameWith(String password, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(password, this.password);
    }

    /**
     * 비밀번호 검사기로 주어진 비밀번호가 저장된 비밀번호와 일치하고 삭제되지 않았는지 여부를 반환한다.
     *
     * @param password 비밀번호
     * @param passwordEncoder 비밀번호 검사기
     * @return 주어진 비밀번호가 저장된 비밀번호와 일치하고 삭제되지 않았는지 여부
     */
    public boolean authenticate(String password, PasswordEncoder passwordEncoder) {
        return !this.deleted && passwordEncoder.matches(password, this.password);
    }

    /**
     * 주어진 비밀번호를 비밀번호 검사를 통해 암호화해서 수정한다.
     *
     * @param password 비밀번호
     * @param passwordEncoder 비밀번호 검사기
     */
    public void updatePassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    /**
     * 주어진 스터디를 사용자에 추가한다.
     *
     * @param study 스터디
     */
    public void addStudy(Study study) {
        this.study = study;
    }

    /**
     * 사용자의 스터디를 삭제한다.
     */
    public void cancelStudy() {
        this.study = null;
    }

    /**
     * 주어진 업로드 파일을 사용자 업로드 파일에 추가한다.
     *
     * @param uploadFile 업로드 파일
     */
    public void addUploadFile(UploadFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    /**
     * 주어진 닉네임으로 사용자 닉네임을 수정한다.
     *
     * @param nickname 닉네임
     */
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 주어진 스터디가 사용자가 생성했는지 여부를 반환한다.
     *
     * @param study 스터디
     * @return 주어진 스터디가 사용자가 생성했는지 여부
     */
    public boolean isMangerOf(Study study) {
        return this.study != null && this.email.equals(study.getEmail());
    }

    /**
     * 주어진 스터디가 사용자가 참여했는지 여부를 반환한다.
     *
     * @param study 스터디
     * @return 주어진 스터디에 사용자가 참여하는지 여부
     */
    public boolean isApplierOf(Study study) {
        return this.study != null && this.study.getId().equals(study.getId())
                && !this.email.equals(study.getEmail());
    }

    /**
     * 사용자의 스터디를 삭제한다.
     */
    public void deleteStudy() {
        this.study = null;
    }
}
