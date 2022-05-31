package com.example.bookclub.domain.study.studylike;

import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.study.Study;

import java.util.Optional;

public interface StudyLikeRepository {
	StudyLike save(StudyLike studyLike);

	Optional<StudyLike> findByStudyAndAccount(Study study, Account account);

	void delete(StudyLike studyLike);
}
