package com.example.bookclub.controller.api;

import com.example.bookclub.application.StudyService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Study;
import com.example.bookclub.dto.StudyCreateDto;
import com.example.bookclub.dto.StudyResultDto;
import com.example.bookclub.dto.StudyUpdateDto;
import com.example.bookclub.security.CurrentAccount;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/study")
public class StudyApiController {
    private final StudyService studyService;

    public StudyApiController(StudyService studyService) {
        this.studyService = studyService;
    }

    @GetMapping
    public List<Study> list() {
        return studyService.getStudies();
    }

    @GetMapping("/{id}")
    public StudyResultDto detail(@PathVariable Long id) {
        Study study = studyService.getStudy(id);
        return StudyResultDto.of(study);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudyResultDto create(@CurrentAccount Account account,
                                 @RequestBody StudyCreateDto studyCreateDto)
            throws ParseException {
        return studyService.createStudy(account, studyCreateDto);
    }

    @PatchMapping("/{id}")
    public StudyResultDto update(@CurrentAccount Account account,
                                 @PathVariable Long id,
                                 @RequestBody StudyUpdateDto studyUpdateDto
    ) throws ParseException {
        return studyService.updateStudy(account, id, studyUpdateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public StudyResultDto delete(@CurrentAccount Account account,
                                 @PathVariable Long id) {
        return studyService.deleteStudy(account, id);
    }

    @PostMapping("/apply/{id}")
    public Long apply(@CurrentAccount Account account,
                      @PathVariable Long id) {
        return studyService.applyStudy(account, id);
    }

    @DeleteMapping("/apply/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Long cancel(@CurrentAccount Account account,
                       @PathVariable Long id) {
        return studyService.cancelStudy(account, id);
    }
}
