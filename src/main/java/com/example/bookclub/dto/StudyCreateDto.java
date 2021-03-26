package com.example.bookclub.dto;

import com.example.bookclub.domain.Day;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.domain.Zone;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class StudyCreateDto {
    private String name;

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
}
