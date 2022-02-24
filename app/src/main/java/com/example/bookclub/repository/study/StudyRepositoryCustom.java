package com.example.bookclub.repository.study;

import com.example.bookclub.dto.StudyInfoResultDto;

public interface StudyRepositoryCustom {
    StudyInfoResultDto getStudyInfo(Long id);
}
