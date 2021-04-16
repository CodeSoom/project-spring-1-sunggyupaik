package com.example.bookclub.domain;

import com.example.bookclub.dto.StudyUpdateDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "accounts")
@Builder
public class Study {
    @Id
    @GeneratedValue
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

    @OneToMany(mappedBy = "study")
    @Builder.Default
    List<Account> accounts = new ArrayList<>();

    @Builder
    public Study(Long id, String name, String bookName, String bookImage, String email, String description,
                 String contact, int size, int applyCount, LocalDate startDate, LocalDate endDate, String startTime,
                 String endTime, Day day, StudyState studyState, Zone zone, List<Account> accounts) {
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

    public void addAdmin(String email) {
        this.email = email;
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
}
