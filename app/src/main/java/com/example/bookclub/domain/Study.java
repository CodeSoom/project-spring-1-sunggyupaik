package com.example.bookclub.domain;

import com.example.bookclub.common.BaseEntity;
import com.example.bookclub.dto.StudyApiDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 스터디
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString
@JsonIgnoreProperties({"accounts", "studyLikes", "studyComments"})
public class Study extends BaseEntity {
    /* 식별자 */
    @Id @GeneratedValue
    @Column(name = "STUDY_ID")
    private Long id;

    /* 이름 */
    private String name;

    /* 책 제목 */
    private String bookName;

    /* 책 사진 */
    private String bookImage;

    /* 이메일 */
    private String email;

    /* 설명 */
    private String description;

    /* 연락처 */
    private String contact;

    /* 정원 수 */
    private int size;

    /* 지원 수 */
    @Builder.Default
    private int applyCount = 0;

    /* 시작날짜 */
    private LocalDate startDate;

    /* 종료날짜 */
    private LocalDate endDate;

    /* 시작시간 */
    private String startTime;

    /* 종료시간 */
    private String endTime;

    /* 요일 */
    @Enumerated(EnumType.STRING)
    private Day day;

    /* 스터디 상태 */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StudyState studyState = StudyState.OPEN;

    /* 지역 */
    @Enumerated(EnumType.STRING)
    private Zone zone;

    /* 사용자 리스트 */
    @OneToMany(mappedBy = "study", cascade = CascadeType.REMOVE)
    @Builder.Default
    @ToString.Exclude
    List<Account> accounts = new ArrayList<>();

    /* 스터디 좋아요 리스트 */
    @OneToMany(mappedBy = "study")
    @Builder.Default
    @ToString.Exclude
    List<StudyLike> studyLikes = new ArrayList<>();

    /* 스터디 댓글 리스트 */
    @OneToMany(mappedBy = "study")
    @Builder.Default
    @ToString.Exclude
    List<StudyComment> studyComments = new ArrayList<>();

    /* 즐겨찾기 리스트 */
    @OneToMany(mappedBy = "study")
    @Builder.Default
    @ToString.Exclude
    List<Favorite> favorites = new ArrayList<>();

    /* 좋아요 여부 */
    @Transient
    private boolean liked;

    /* 좋아요 수 */
    @Transient
    private int likesCount;

    /* 댓글 수 */
    @Transient
    private int commentsCount;

    /* 즐겨찾기 여부 */
    @Transient
    private boolean isFavorite;

    @Builder
    @QueryProjection
    public Study(Long id, String name, String bookName, String bookImage, String email, String description, String contact,
                 int size, int applyCount, LocalDate startDate, LocalDate endDate, String startTime, String endTime,
                 Day day, StudyState studyState, Zone zone, List<Account> accounts, List<StudyLike> studyLikes,
                 List<StudyComment> studyComments, List<Favorite> favorites, boolean liked, int likesCount, int commentsCount,
                 boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.bookName = bookName;
        this.bookImage = bookImage;
        this.email = email;
        this.description = description;
        this.contact = contact;
        this.size = size;
        this.applyCount = applyCount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
        this.studyState = studyState;
        this.zone = zone;
        this.accounts = accounts;
        this.studyLikes = studyLikes;
        this.studyComments = studyComments;
        this.favorites = favorites;
        this.liked = liked;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.isFavorite = isFavorite;
    }

    /**
     * 주어진 수정할 스터디 정보로 저장된 스터디를 수정한다.
     *
     * @param studyUpdateDto 수정할 스터디 정보
     */
    public void updateWith(StudyApiDto.StudyUpdateDto studyUpdateDto) {
        this.name = studyUpdateDto.getName();
        this.description = studyUpdateDto.getDescription();
        this.contact = studyUpdateDto.getContact();
        this.size = studyUpdateDto.getSize();
        this.startDate = studyUpdateDto.getStartDate();
        this.endDate = studyUpdateDto.getEndDate();
        this.day = studyUpdateDto.getDay();
        this.studyState = studyUpdateDto.getStudyState();
        this.zone = studyUpdateDto.getZone();
    }

    /**
     * 주어진 사용자를 저장된 스터디에 추가한다.
     * 지원자 수를 1 증가시킨다.
     *
     * @param account 사용자
     */
    public void addAccount(Account account) {
        this.applyCount += 1;
        accounts.add(account);
        account.addStudy(this);
    }

    /**
     * 주어진 사용자를 저장된 스터디에 삭제한다.
     * 지원자 수를 1 감소시킨다.
     *
     * @param account 사용자
     */
    public void cancelAccount(Account account) {
        this.applyCount -= 1;
        accounts.remove(account);
        account.cancelStudy();
    }

    /**
     * 주어진 사용자 이메일을 저장된 스터디의 이메일에 추가한다.
     *
     * @param account 사용자
     */
    public void addAdmin(Account account) {
        this.email = account.getEmail();
        account.addStudy(this);
    }

    /**
     * 저장된 스터디 정원 여부를 반환한다.
     *
     * @return 저장된 스터디 정원 여부
     */
    public boolean isSizeFull() {
        return this.applyCount == this.size;
    }

    /**
     * 저장된 스터디의 스터디 상태를 진행중으로 수정한다.
     */
    public void changeOpenToClose() {
        this.studyState = StudyState.CLOSE;
    }

    /**
     * 저장된 스터디의 스터디 상태를 종료로 수정한다.
     */
    public void changeCloseToEnd() {
        this.studyState = StudyState.END;
    }

    /**
     * 저장된 스터디의 시작날짜가 오늘이거나 과거인지 여부를 반환한다.
     *
     * @return 저장된 스터디의 시작날짜가 오늘이거나 과거인지 여부
     */
    public boolean isAlreadyStarted() {
        LocalDate startDate = this.startDate;
        LocalDate nowDate = LocalDate.now();

        return startDate.isEqual(nowDate) || startDate.isBefore(nowDate);
    }

    /**
     * 스터디가 모집중이 아닌지 여부를 반환한다.
     *
     * @return 스터디가 모집중이 아닌지 여부
     */
    public boolean isNotOpened() {
        return !this.studyState.equals(StudyState.OPEN);
    }

    /**
     * 저장된 스터디의 사용자를 모두 삭제한다.
     */
    public void deleteAccounts() {
        for(Account account : accounts) {
            account.deleteStudy();
        }
    }

    /**
     * 저장된 스터디에 좋아요 여부를 참으로 수정한다.
     */
    public void addLiked() {
        this.liked = true;
    }

    /**
     * 저장된 스터디에 주어진 좋아요 수를 추가한다.
     *
     * @param likesCount 좋아요 수
     */
    public void addLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    /**
     * 저장된 스터디에 주어진 댓글 수를 추가한다.
     *
     * @param commentsCount 댓글 수
     */
    public void addCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    /**
     * 저장된 스터디의 즐겨찾기를 참으로 수정한다.
     */
    public void addFavorite() {
        this.isFavorite = true;
    }
}
