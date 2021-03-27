package com.example.bookclub.controllers;

import com.example.bookclub.application.StudyService;
import com.example.bookclub.dto.StudyCreateDto;
import com.example.bookclub.dto.StudyResultDto;
import com.example.bookclub.dto.StudyUpdateDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/study")
public class StudyApiController {
    private final StudyService studyService;

    public StudyApiController(StudyService studyService) {
        this.studyService = studyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudyResultDto create(@RequestBody StudyCreateDto studyCreateDto) {
        return studyService.createStudy(studyCreateDto);
    }

    @PatchMapping("/{id}")
    public StudyResultDto update(@PathVariable Long id,
                                 @RequestBody StudyUpdateDto studyUpdateDto
    ) {
        return studyService.updateStudy(id, studyUpdateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public StudyResultDto delete(@PathVariable Long id) {
        return studyService.deleteStudy(id);
    }
}
