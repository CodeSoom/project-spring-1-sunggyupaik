package com.example.bookclub.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UploadFileCreateDto {
	private String fileName;

	private String fileOriginalName;

	private String fileUrl;

	@Builder
	public UploadFileCreateDto(String fileName, String fileOriginalName, String fileUrl) {
		this.fileName = fileName;
		this.fileOriginalName = fileOriginalName;
		this.fileUrl = fileUrl;
	}
}
