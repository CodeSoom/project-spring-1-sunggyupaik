package com.example.bookclub.domain;

import com.example.bookclub.common.BaseEntity;
import com.example.bookclub.dto.StudyUpdateDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString
@JsonIgnoreProperties({"accounts", "studyLikes", "studyComments"})
public class Study extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "STUDY_ID")
    private Long id;

    private String name;

    private String bookName;

    private String bookImage;

    private String email;

    private String description;

    private String contact;

    private int size;

    @Builder.Default
    private int applyCount = 0;

    private LocalDate startDate;

    private LocalDate endDate;

    private String startTime;

    private String endTime;

    @Enumerated(EnumType.STRING)
    private Day day;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StudyState studyState = StudyState.OPEN;

    @Enumerated(EnumType.STRING)
    private Zone zone;

    @OneToMany(mappedBy = "study", cascade = CascadeType.REMOVE)
    @Builder.Default
    @ToString.Exclude
    List<Account> accounts = new ArrayList<>();

    @OneToMany(mappedBy = "study")
    @ToString.Exclude
    List<StudyLike> studyLikes = new ArrayList<>();

    @OneToMany(mappedBy = "study")
    @ToString.Exclude
    List<StudyComment> studyComments = new ArrayList<>();

    @OneToMany(mappedBy = "study")
    @ToString.Exclude
    List<Favorite> favorites = new ArrayList<>();
    
    @Transient
    private boolean liked;

    @Transient
    private int likesCount;

    @Transient
    private int commentsCount;

    @Transient
    private boolean isFavorite;

    @Builder
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

    public void updateWith(StudyUpdateDto studyUpdateDto) {
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

    public boolean isManagedBy(Account account) {
        return this.email.equals(account.getEmail());
    }

    public void addAccount(Account account) {
        this.applyCount += 1;
        accounts.add(account);
        account.addStudy(this);
    }

    public void cancelAccount(Account account) {
        this.applyCount -= 1;
        accounts.remove(account);
        account.cancelStudy();
    }

    public void addAdmin(Account account) {
        this.email = account.getEmail();
        account.addStudy(this);
    }

    public boolean isSizeFull() {
        return this.applyCount == this.size;
    }

    public boolean isOneLeft() {
        return this.applyCount + 1 == this.size;
    }

    public void changeOpenToClose() {
        this.studyState = StudyState.CLOSE;
    }

    public void changeCloseToOpen() {
        this.studyState = StudyState.OPEN;
    }

    public void changeCloseToEnd() {
        this.studyState = StudyState.END;
    }

    public boolean isAlreadyStarted() {
        LocalDate startDate = this.startDate;
        LocalDate nowDate = LocalDate.now();

        return startDate.isEqual(nowDate) || startDate.isBefore(nowDate);
    }

    public boolean isNotOpened() {
        return !this.studyState.equals(StudyState.OPEN);
    }

    public void deleteAccounts() {
        for(Account account : accounts) {
            account.deleteStudy();
        }
    }

    public void addLiked() {
        this.liked = true;
    }

    public void addLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public void addCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }
}
