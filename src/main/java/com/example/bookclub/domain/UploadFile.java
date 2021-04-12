package com.example.bookclub.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "account")
public class UploadFile {
    @Id
    @GeneratedValue
    private Long id;

    private String fileName;

    private String fileOriginalName;

    private String fileUrl;

    @OneToOne(mappedBy = "uploadFile")
    private Account account;

    @Builder
    public UploadFile(String fileName, String fileOriginalName, String fileUrl) {
        this.fileName = fileName;
        this.fileOriginalName = fileOriginalName;
        this.fileUrl = fileUrl;
    }

    public void addAccount(Account savedAccount) {
        this.account = savedAccount;
        savedAccount.addUploadFile(this);
    }
}
