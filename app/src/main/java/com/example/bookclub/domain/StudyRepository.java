package com.example.bookclub.domain;

import com.example.bookclub.dto.StudyInfoResultDto;

public interface StudyRepository {
    StudyInfoResultDto getStudyInfoById(Long id);
}
