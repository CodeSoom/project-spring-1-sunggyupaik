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

/**
 * 인터뷰
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Interview extends BaseTimeEntity {
    /* 식별자 */
    @Id @GeneratedValue
    @Column(name = "INTERVIEW_ID")
    private Long id;

    /* 인터뷰 URL */
    private String interviewUrl;

    /* 이미지 URL */
    private String imgUrl;

    /* 작가 */
    private String author;

    /* 제목 */
    private String title;

    /* 날짜 */
    private LocalDate date;

    /* 내용 */
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
