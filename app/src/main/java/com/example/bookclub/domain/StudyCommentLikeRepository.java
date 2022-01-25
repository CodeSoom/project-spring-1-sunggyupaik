package com.example.bookclub.domain;

import java.util.Optional;

public interface StudyCommentLikeRepository {
	StudyCommentLike save(StudyCommentLike studyLike);

	Optional<StudyCommentLike> findByStudyCommentAndAccount(StudyComment studyComment, Account account);

	Optional<StudyCommentLike> findById(Long StudyCommentLikeId);

	void delete(StudyCommentLike studyCommentLike);
}
