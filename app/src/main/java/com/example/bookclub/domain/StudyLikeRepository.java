package com.example.bookclub.domain;

import java.util.Optional;

public interface StudyLikeRepository {
	StudyLike save(StudyLike studyLike);

	Optional<StudyLike> findByStudyAndAccount(Study study, Account account);

	void delete(StudyLike studyLike);
}
