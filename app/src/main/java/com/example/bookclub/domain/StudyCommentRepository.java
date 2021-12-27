package com.example.bookclub.domain;

import java.util.List;

public interface StudyCommentRepository {
	StudyComment save(StudyComment studyComment);

	List<StudyComment> findByStudyId(Long id);

	void delete(StudyComment studyComment);
}
