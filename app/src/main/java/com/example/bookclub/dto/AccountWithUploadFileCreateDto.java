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
public class AccountWithUploadFileCreateDto {
	private AccountCreateDto accountCreateDto;
	private MultipartFile multipartFile;

	@Builder
	public AccountWithUploadFileCreateDto(AccountCreateDto accountCreateDto,
										  MultipartFile multipartFile) {
		this.accountCreateDto = accountCreateDto;
		this.multipartFile = multipartFile;
	}
}
