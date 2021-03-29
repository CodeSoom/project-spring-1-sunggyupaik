package com.example.bookclub.application;

import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyRepository;
import com.example.bookclub.dto.StudyCreateDto;
import com.example.bookclub.dto.StudyResultDto;
import com.example.bookclub.dto.StudyUpdateDto;
import com.example.bookclub.errors.StudyNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class StudyService {
    private final StudyRepository studyRepository;

    public StudyService(StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

    public StudyResultDto createStudy(StudyCreateDto studyCreateDto) {
        Study study = studyCreateDto.toEntity();
        Study createdStudy = studyRepository.save(study);
        return StudyResultDto.of(createdStudy);
    }

    public StudyResultDto updateStudy(Long id, StudyUpdateDto studyUpdateDto) {
        Study study = getStudy(id);

        study.updateWith(studyUpdateDto);

        return StudyResultDto.of(study);
    }

    public StudyResultDto deleteStudy(Long id) {
        Study study = getStudy(id);
        studyRepository.delete(study);
        return StudyResultDto.of(study);
    }

    public Study getStudy(Long id) {
        return studyRepository.findById(id)
                .orElseThrow(() -> new StudyNotFoundException(id));
    }

    public List<Study> getStudies() {
        return studyRepository.findAll();
    }
}
