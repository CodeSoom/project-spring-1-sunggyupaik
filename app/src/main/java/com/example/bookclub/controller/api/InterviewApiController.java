package com.example.bookclub.controller.api;

import com.example.bookclub.application.interview.InterviewService;
import com.example.bookclub.common.response.CommonResponse;
import com.example.bookclub.domain.interview.Interview;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 인터뷰 크롤링을 요청한다
 */
@RestController
@RequestMapping("/api/interviews")
public class InterviewApiController {
	private final InterviewService interviewService;

	public InterviewApiController(InterviewService interviewService) {
		this.interviewService = interviewService;
	}

	/**
	 * 인터뷰를 크롤링한다
	 *
	 * @return 크롤링한 인터뷰
	 * @throws AccessDeniedException ADMIN 권한이 아닌 경우
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CommonResponse<List<Interview>> create() {
		List<Interview> response = interviewService.crawlAllInterviews();
		return CommonResponse.success(response);
	}
}
