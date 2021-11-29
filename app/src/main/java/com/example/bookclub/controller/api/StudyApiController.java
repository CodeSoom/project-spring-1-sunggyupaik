package com.example.bookclub.controller.api;

import com.example.bookclub.application.StudyService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Study;
import com.example.bookclub.dto.StudyCreateDto;
import com.example.bookclub.dto.StudyResultDto;
import com.example.bookclub.dto.StudyUpdateDto;
import com.example.bookclub.security.CurrentAccount;
import com.example.bookclub.security.UserAccount;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
                                 @RequestBody StudyCreateDto studyCreateDto) {
        return studyService.createStudy(account.getEmail(), studyCreateDto);
    }

    //@PreAuthorize("@studyManagerCheck.isManagerOfStudy(#userAccount.account)")
    @PatchMapping("/{id}")
    public StudyResultDto update(@AuthenticationPrincipal UserAccount userAccount,
                                 @PathVariable Long id,
                                 @RequestBody StudyUpdateDto studyUpdateDto) {
        return studyService.updateStudy(userAccount.getAccount().getEmail(), id, studyUpdateDto);
    }

    //@PreAuthorize("@studyManagerCheck.isManagerOfStudy(#userAccount.account)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public StudyResultDto delete(@AuthenticationPrincipal UserAccount userAccount,
                                 @PathVariable Long id) {
        return studyService.deleteStudy(userAccount.getAccount().getEmail(), id);
    }

    @PostMapping("/apply/{id}")
    public Long apply(@AuthenticationPrincipal UserAccount userAccount,
                      @PathVariable Long id) {
        return studyService.applyStudy(userAccount, id);
    }

    @DeleteMapping("/apply/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Long cancel(@CurrentAccount Account account,
                       @PathVariable Long id) {
        return studyService.cancelStudy(account, id);
    }
}
