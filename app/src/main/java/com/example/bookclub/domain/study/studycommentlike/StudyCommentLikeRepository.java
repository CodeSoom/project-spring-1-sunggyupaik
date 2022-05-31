package com.example.bookclub.domain.study.studycommentlike;

import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.study.studycomment.StudyComment;

import java.util.Optional;

public interface StudyCommentLikeRepository {
	StudyCommentLike save(StudyCommentLike studyLike);

	Optional<StudyCommentLike> findByStudyCommentAndAccount(StudyComment studyComment, Account account);

	Optional<StudyCommentLike> findById(Long StudyCommentLikeId);

	void delete(StudyCommentLike studyCommentLike);
}
