package com.example.bookclub.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Study {
    @Id
    @GeneratedValue
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
}
