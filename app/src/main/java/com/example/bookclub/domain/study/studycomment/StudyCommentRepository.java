package com.example.bookclub.domain.study.studycomment;

import java.util.List;
import java.util.Optional;

public interface StudyCommentRepository {
	StudyComment save(StudyComment studyComment);

	List<StudyComment> findByStudyId(Long id);

	void delete(StudyComment studyComment);

	Optional<StudyComment> findById(Long id);
}
