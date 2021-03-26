package com.example.bookclub.dto;

import com.example.bookclub.domain.Day;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.domain.Zone;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class StudyResultDto {
    private Long id;

    private String name;

    private String description;

    private String contact;

    private int size;

    private LocalDate startDate;

    private LocalDate endDate;

    private String startTime;

    private String endTime;

    private Day day;

    private StudyState studyState;

    private Zone zone;

    public StudyResultDto(Long id, String name, String description, String contact,
                          int size, LocalDate startDate, LocalDate endDate, String startTime,
                          String endTime, Day day, StudyState studyState, Zone zone) {
        this.id = id;
        this.name = name;
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

    public static StudyResultDto of(Study study) {
        return StudyResultDto.builder()
                .id(study.getId())
                .name(study.getName())
                .description(study.getDescription())
                .contact(study.getContact())
                .size(study.getSize())
                .startDate(study.getStartDate())
                .endDate(study.getEndDate())
                .startTime(study.getStartTime())
                .endTime(study.getEndTime())
                .day(study.getDay())
                .studyState(study.getStudyState())
                .zone(study.getZone())
                .build();
    }


}
