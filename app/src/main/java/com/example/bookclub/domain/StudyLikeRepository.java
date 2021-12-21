package com.example.bookclub.domain;

import java.util.Optional;

public interface StudyLikeRepository {
	StudyLike save(StudyLike studyLike);

	Optional<StudyLike> findByStudyIdAndAccountId(Long studyId, Long AccountId);
}
