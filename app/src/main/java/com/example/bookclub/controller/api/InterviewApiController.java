package com.example.bookclub.controller.api;

import com.example.bookclub.application.InterviewService;
import com.example.bookclub.domain.Interview;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
public class InterviewApiController {
	private final InterviewService interviewService;

	public InterviewApiController(InterviewService interviewService) {
		this.interviewService = interviewService;
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping
	public List<Interview> create() {
		return interviewService.crawlAllInterviews();
	}
}
