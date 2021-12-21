package com.example.bookclub.infra;

import com.example.bookclub.domain.StudyLike;
import com.example.bookclub.domain.StudyLikeRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaStudyLikeRepository
		extends StudyLikeRepository, CrudRepository<StudyLike, Long> {
	StudyLike save(StudyLike studyLike);

	Optional<StudyLike> findByStudyIdAndAccountId(Long studyId, Long AccountId);
}
