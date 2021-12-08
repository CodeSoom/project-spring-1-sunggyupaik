package com.example.bookclub.domain;

import com.example.bookclub.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Interview extends BaseTimeEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String interviewUrl;

    private String imgUrl;

    private String author;

    private String title;

    private LocalDate date;

    @Column(length = 1000)
    private String content;

    @Builder
    public Interview(Long id, String interviewUrl, String imgUrl, String author,
                     String title, LocalDate date, String content) {
        this.id = id;
        this.interviewUrl = interviewUrl;
        this.imgUrl = imgUrl;
        this.author = author;
        this.title = title;
        this.date = date;
        this.content = content;
    }
}
