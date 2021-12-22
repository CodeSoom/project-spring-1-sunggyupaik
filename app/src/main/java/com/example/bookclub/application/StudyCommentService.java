package com.example.bookclub.application;

import com.example.bookclub.dto.StudyCommentCreateDto;
import com.example.bookclub.dto.StudyCommentResultDto;
import com.example.bookclub.security.UserAccount;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class StudyCommentService {
	@Transactional
	public StudyCommentResultDto createStudyComment(UserAccount userAccount, Long studyId,
													StudyCommentCreateDto studyCommentCreateDto) {
		return null;
	}
}
