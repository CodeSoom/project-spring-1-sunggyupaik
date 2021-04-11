package com.example.bookclub.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UploadFile {
    @Id
    @GeneratedValue
    private Long id;

    private String fileName;

    private String fileOriginalName;

    private String fileUrl;

    @Builder
    public UploadFile(String fileName, String fileOriginalName, String fileUrl) {
        this.fileName = fileName;
        this.fileOriginalName = fileOriginalName;
        this.fileUrl = fileUrl;
    }
}
