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
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Study {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String email;

    private String description;

    private String contact;

    private int size;

    private LocalDate startDate;

    private LocalDate endDate;

    private String startTime;

    private String endTime;

    @Enumerated(EnumType.STRING)
    private Day day;

    @Enumerated(EnumType.STRING)
    private StudyState studyState;

    @Enumerated(EnumType.STRING)
    private Zone zone;

    @Builder
    public Study(Long id, String name, String email, String description, String contact,
                 int size, LocalDate startDate, LocalDate endDate, String startTime,
                 String endTime, Day day, StudyState studyState, Zone zone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.description = description;
        this.contact = contact;
        this.size = size;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
        this.studyState = studyState;
        this.zone = zone;
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
}
