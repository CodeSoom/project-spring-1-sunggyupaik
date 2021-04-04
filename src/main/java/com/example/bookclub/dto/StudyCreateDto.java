package com.example.bookclub.dto;

import com.example.bookclub.domain.Day;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.domain.Zone;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyCreateDto {
    private String name;

    private String email;

    @Size(min=10, max=1000)
    private String description;

    @Size(max=50)
    private String contact;

    @Min(1) @Max(20)
    private int size;

    private LocalDate startDate;

    private LocalDate endDate;

    private String startTime;

    private String endTime;

    private Day day;

    private StudyState studyState;

    private Zone zone;

    public Study toEntity() {
        return Study.builder()
                .name(this.name)
                .email(this.email)
                .description(this.description)
                .contact(this.contact)
                .size(this.size)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .day(this.day)
                .studyState(this.studyState)
                .zone(this.zone)
                .build();
    }

    @Builder
    public StudyCreateDto(String name, String email, String description, String contact,
                          int size, LocalDate startDate, LocalDate endDate, String startTime,
                          String endTime, Day day, StudyState studyState, Zone zone) {
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
}
