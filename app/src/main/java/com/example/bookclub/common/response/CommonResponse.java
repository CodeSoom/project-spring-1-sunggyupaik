package com.example.bookclub.common.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommonResponse<T> {
	private Result result;
	private T data;
	private String message;
	private String errorCode;

	@Builder
	public CommonResponse(Result result, T data, String message, String errorCode) {
		this.result = result;
		this.data = data;
		this.message = message;
		this.errorCode = errorCode;
	}

	public static <T> CommonResponse<T> success(T data, String message) {
		return (CommonResponse<T>) CommonResponse.builder()
				.result(Result.SUCCESS)
				.data(data)
				.message(message)
				.build();
	}

	public static <T> CommonResponse<T> success(T data) {
		return success(data, null);
	}

	public static CommonResponse fail(String message, String errorCode) {
		return CommonResponse.builder()
				.result(Result.FAIL)
				.message(message)
				.errorCode(errorCode)
				.build();
	}

	public static CommonResponse fail(String message, int errorCode) {
		return CommonResponse.builder()
				.result(Result.FAIL)
				.message(message)
				.errorCode(Integer.toString(errorCode))
				.build();
	}

	public enum Result {
		SUCCESS, FAIL
	}
}
