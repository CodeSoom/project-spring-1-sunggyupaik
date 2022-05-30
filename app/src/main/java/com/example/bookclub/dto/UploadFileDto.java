package com.example.bookclub.dto;

import com.example.bookclub.domain.UploadFile;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UploadFileDto {
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class UploadFileCreateDto {
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

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class UploadFileResultDto {
		private Long id;

		private String fileName;

		private String fileOriginalName;

		private String fileUrl;

		@Builder
		public UploadFileResultDto(Long id, String fileName, String fileOriginalName, String fileUrl) {
			this.id = id;
			this.fileName = fileName;
			this.fileOriginalName = fileOriginalName;
			this.fileUrl = fileUrl;
		}

		public static UploadFileResultDto of(UploadFile uploadFile) {
			if(uploadFile == null)
				return UploadFileResultDto.builder()
						.fileName("")
						.fileOriginalName("")
						.fileUrl("")
						.build();

			return UploadFileResultDto.builder()
					.id(uploadFile.getId())
					.fileName(uploadFile.getFileName())
					.fileOriginalName(uploadFile.getFileOriginalName())
					.fileUrl(uploadFile.getFileUrl())
					.build();
		}
	}
}
