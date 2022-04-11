package com.example.bookclub.dto;

import com.example.bookclub.domain.Day;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.domain.Zone;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyResultDto {
    private Long id;

    private String name;

    private String bookName;

    private String bookImage;

    private String email;

    private String description;

    private String contact;

    private int size;

    private int applyCount;

    private LocalDate startDate;

    private LocalDate endDate;

    private String startTime;

    private String endTime;

    private Day day;

    private StudyState studyState;

    private Zone zone;

    private int likesCount;

    private boolean liked;

    private int commentsCount;

    private boolean isFavorite;

    @Builder
    @QueryProjection
    public StudyResultDto(Long id, String name, String bookName, String bookImage, String email, String description,
                String contact, int size, int applyCount, LocalDate startDate, LocalDate endDate, String startTime,
                String endTime, Day day, StudyState studyState, Zone zone, int likesCount, boolean liked,
        int commentsCount, boolean isFavorite) {
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
        this.likesCount = likesCount;
        this.liked = liked;
        this.commentsCount = commentsCount;
        this.isFavorite = isFavorite;
    }

    @QueryProjection
    public StudyResultDto(Long id, String name, String bookName, String bookImage, String email, String description,
                          String contact, int size, int applyCount, LocalDate startDate, LocalDate endDate,
                          String startTime, String endTime, Day day, StudyState studyState, Zone zone) {
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
    }

    public static StudyResultDto of(Study study) {
        if(study == null) {
            return StudyResultDto.builder().build();
        }

        return StudyResultDto.builder()
                .id(study.getId())
                .name(study.getName())
                .bookName(study.getBookName())
                .bookImage(study.getBookImage())
                .email(study.getEmail())
                .description(study.getDescription())
                .contact(study.getContact())
                .size(study.getSize())
                .applyCount(study.getApplyCount())
                .startDate(study.getStartDate())
                .endDate(study.getEndDate())
                .startTime(study.getStartTime())
                .endTime(study.getEndTime())
                .day(study.getDay())
                .studyState(study.getStudyState())
                .zone(study.getZone())
                .likesCount(study.getStudyLikes() == null ? 0 : study.getStudyLikes().size())
                .liked(study.isLiked())
                .commentsCount(study.getCommentsCount())
                .isFavorite(study.isFavorite())
                .build();
    }
}
