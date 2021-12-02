package com.example.bookclub.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class AccountWithUploadFileUpdateDto {
	private AccountUpdateDto accountUpdateDto;
	private MultipartFile multipartFile;

	@Builder
	public AccountWithUploadFileUpdateDto(AccountUpdateDto accountUpdateDto,
										  MultipartFile multipartFile) {
		this.accountUpdateDto = accountUpdateDto;
		this.multipartFile = multipartFile;
	}
}
