package com.example.bookclub.application;

import com.example.bookclub.domain.StudyRepository;
import com.example.bookclub.dto.StudyCreateDto;
import com.example.bookclub.dto.StudyResultDto;
import org.springframework.stereotype.Service;

@Service
public class StudyService {
    private final StudyRepository studyRepository;

    public StudyService(StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

    public StudyResultDto createStudy(StudyCreateDto studyCreateDto) {
        return null;
    }
}
