package com.example.bookclub.repository.study;

import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.dto.StudyInfoResultDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudyRepositoryCustom {
    StudyInfoResultDto getStudyInfo(Long id);

    Page<Study> findByBookNameContaining(String keyword, Pageable pageable);

    Page<Study> findByStudyState(StudyState studyState, Pageable pageable);

    long getStudiesCount(StudyState studyState);

    long getAllStudiesCount();
}
