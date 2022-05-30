package com.example.bookclub.dto;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.UploadFile;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;

public class AccountDto {
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class AccountCreateDto {
		@Size(min = 2, max = 6)
		private String name;

		@Size(min = 3, max = 48)
		private String email;

		@Size(min = 3, max = 10)
		private String nickname;

		@Size(min = 4, max = 1024)
		private String password;

		private String authenticationNumber;

		@Builder
		public AccountCreateDto(String name, String email, String nickname,
								String password, String authenticationNumber) {
			this.name = name;
			this.email = email;
			this.nickname = nickname;
			this.password = password;
			this.authenticationNumber = authenticationNumber;
		}

		public Account toEntity() {
			return Account.builder()
					.name(this.name)
					.email(this.email)
					.nickname(this.nickname)
					.password(this.password)
					.deleted(false)
					.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class AccountCreateResultDto {
		private Long id;

		private String name;

		private String email;

		private String nickname;

		private String password;

		private boolean deleted;

		private UploadFileDto.UploadFileResultDto uploadFileResultDto;

		@Builder
		public AccountCreateResultDto(Long id, String name, String email, String nickname, String password,
									  boolean deleted, UploadFileDto.UploadFileResultDto uploadFileResultDto) {
			this.id = id;
			this.name = name;
			this.email = email;
			this.nickname = nickname;
			this.password = password;
			this.deleted = deleted;
			this.uploadFileResultDto = uploadFileResultDto;
		}

		public static AccountCreateResultDto of(Account account) {
			return AccountCreateResultDto.builder()
					.id(account.getId())
					.name(account.getName())
					.email(account.getEmail())
					.nickname(account.getNickname())
					.password(account.getPassword())
					.deleted(account.isDeleted())
					.uploadFileResultDto(UploadFileDto.UploadFileResultDto.of(account.getUploadFile()))
					.build();
		}

		public void setUploadFileResultDto(UploadFile uploadFile) {
			if (uploadFile == null) this.uploadFileResultDto = null;
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class AccountDeleteResultDto {
		private Long id;

		private String name;

		private String email;

		private String nickname;

		private String password;

		private boolean deleted;

		private UploadFileDto.UploadFileResultDto uploadFileResultDto;

		@Builder
		public AccountDeleteResultDto(Long id, String name, String email, String nickname, String password,
									  boolean deleted, UploadFileDto.UploadFileResultDto uploadFileResultDto) {
			this.id = id;
			this.name = name;
			this.email = email;
			this.nickname = nickname;
			this.password = password;
			this.deleted = deleted;
			this.uploadFileResultDto = uploadFileResultDto;
		}

		public static AccountDeleteResultDto of(Account account) {
			return AccountDeleteResultDto.builder()
					.id(account.getId())
					.name(account.getName())
					.email(account.getEmail())
					.nickname(account.getNickname())
					.password(account.getPassword())
					.deleted(account.isDeleted())
					.uploadFileResultDto(UploadFileDto.UploadFileResultDto.of(account.getUploadFile()))
					.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class AccountResultDto {
		private Long id;

		private String name;

		private String email;

		private String nickname;

		private String password;

		private boolean deleted;

		private UploadFileDto.UploadFileResultDto uploadFileResultDto;

		private StudyResultDto studyResultDto;

		@Builder
		public AccountResultDto(Long id, String name, String email, String nickname, String password,
								boolean deleted, UploadFileDto.UploadFileResultDto uploadFileResultDto, StudyResultDto studyResultDto) {
			this.id = id;
			this.name = name;
			this.email = email;
			this.nickname = nickname;
			this.password = password;
			this.deleted = deleted;
			this.uploadFileResultDto = uploadFileResultDto;
			this.studyResultDto = studyResultDto;
		}

		public static AccountResultDto of(Account account) {
			return AccountResultDto.builder()
					.id(account.getId())
					.name(account.getName())
					.email(account.getEmail())
					.nickname(account.getNickname())
					.password(account.getPassword())
					.deleted(account.isDeleted())
					.uploadFileResultDto(UploadFileDto.UploadFileResultDto.of(account.getUploadFile()))
					.studyResultDto(StudyResultDto.of(account.getStudy()))
					.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	@Builder
	public static class AccountUpdateDto {
		@Size(min = 3, max = 10)
		private String nickname;

		private String savedFileName;

		@Builder.Default
		private String password = "";

		@Builder
		public AccountUpdateDto(String nickname, String savedFileName, String password) {
			this.nickname = nickname;
			this.savedFileName = savedFileName;
			this.password = password;
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	@Builder
	public static class AccountUpdatePasswordDto {
		private String password;

		@Size(min=4, max=20)
		private String newPassword;

		@Size(min=4, max=20)
		private String newPasswordConfirmed;

		@Builder
		public AccountUpdatePasswordDto(String password, String newPassword, String newPasswordConfirmed) {
			this.password = password;
			this.newPassword = newPassword;
			this.newPasswordConfirmed = newPasswordConfirmed;
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class AccountUpdatePasswordResultDto {
		private Long id;

		private String name;

		private String email;

		private String nickname;

		private String password;

		private boolean deleted;

		@Builder
		public AccountUpdatePasswordResultDto(Long id, String name, String email,
											  String nickname, String password, boolean deleted) {
			this.id = id;
			this.name = name;
			this.email = email;
			this.nickname = nickname;
			this.password = password;
			this.deleted = deleted;
		}

		public static AccountUpdatePasswordResultDto of(Account account) {
			return AccountUpdatePasswordResultDto.builder()
					.id(account.getId())
					.name(account.getName())
					.email(account.getEmail())
					.nickname(account.getNickname())
					.password(account.getPassword())
					.deleted(account.isDeleted())
					.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class AccountUpdateResultDto {
		private Long id;

		private String name;

		private String email;

		private String nickname;

		private String password;

		private boolean deleted;

		private UploadFileDto.UploadFileResultDto uploadFileResultDto;

		@Builder
		public AccountUpdateResultDto(Long id, String name, String email, String nickname, String password,
									  boolean deleted, UploadFileDto.UploadFileResultDto uploadFileResultDto) {
			this.id = id;
			this.name = name;
			this.email = email;
			this.nickname = nickname;
			this.password = password;
			this.deleted = deleted;
			this.uploadFileResultDto = uploadFileResultDto;
		}

		public static AccountUpdateResultDto of(Account account) {
			return AccountUpdateResultDto.builder()
					.id(account.getId())
					.name(account.getName())
					.email(account.getEmail())
					.nickname(account.getNickname())
					.password(account.getPassword())
					.deleted(account.isDeleted())
					.uploadFileResultDto(UploadFileDto.UploadFileResultDto.of(account.getUploadFile()))
					.build();
		}

		public void setUploadFileResultDto(UploadFileDto.UploadFileResultDto uploadFileResultDto) {
			if(uploadFileResultDto == null) this.uploadFileResultDto = null;
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class AccountWithUploadFileCreateDto {
		private AccountCreateDto accountCreateDto;
		private MultipartFile multipartFile;

		@Builder
		public AccountWithUploadFileCreateDto(AccountCreateDto accountCreateDto,
											  MultipartFile multipartFile) {
			this.accountCreateDto = accountCreateDto;
			this.multipartFile = multipartFile;
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class AccountWithUploadFileUpdateDto {
		private AccountUpdateDto accountUpdateDto;
		private MultipartFile multipartFile;

		@Builder
		public AccountWithUploadFileUpdateDto(AccountUpdateDto accountUpdateDto,
											  MultipartFile multipartFile) {
			this.accountUpdateDto = accountUpdateDto;
			this.multipartFile = multipartFile;
		}
	}

}
