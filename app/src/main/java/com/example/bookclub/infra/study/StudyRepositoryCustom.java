package com.example.bookclub.infra.study;

import com.example.bookclub.dto.StudyInfoResultDto;

public interface StudyRepositoryCustom {
    StudyInfoResultDto getStudyInfo(Long id);
}
