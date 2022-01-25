package com.example.bookclub.application;

import com.example.bookclub.security.UserAccount;
import org.springframework.stereotype.Service;

@Service
public class StudyCommentLikeService {
	private final StudyCommentLikeRepository studyCommentLikeRepository;

	public StudyCommentLikeService(StudyCommentLikeRepository studyCommentLikeRepository) {
		this.studyCommentLikeRepository = studyCommentLikeRepository;
	}

	public StudyCommentLikeResultDto createStudyCommentLike(UserAccount userAccount, Long studyId, Long commentId) {
		return null;
	}
}
