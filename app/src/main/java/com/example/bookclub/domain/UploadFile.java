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
import javax.persistence.OneToOne;

/**
 * 업로드 파일
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UploadFile extends BaseTimeEntity {
    /* 식별자 */
    @Id @GeneratedValue
    @Column(name = "UPLOADFILE_ID")
    private Long id;

    /* 파일명 */
    private String fileName;

    /* 파일 원본명 */
    private String fileOriginalName;

    /* 파일 URL */
    private String fileUrl;

    /* 사용자 */
    @OneToOne(mappedBy = "uploadFile")
    @ToString.Exclude
    private Account account;

    @Builder
    public UploadFile(Long id, String fileName, String fileOriginalName, String fileUrl, Account account) {
        this.id = id;
        this.fileName = fileName;
        this.fileOriginalName = fileOriginalName;
        this.fileUrl = fileUrl;
        this.account = account;
    }
}
