package com.example.bookclub.controller.api;

import com.example.bookclub.application.StudyCommentLikeService;
import com.example.bookclub.application.StudyCommentService;
import com.example.bookclub.application.StudyLikeService;
import com.example.bookclub.application.StudyService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Study;
import com.example.bookclub.dto.StudyCommentCreateDto;
import com.example.bookclub.dto.StudyCommentResultDto;
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
    private final StudyLikeService studyLikeService;
    private final StudyCommentService studyCommentService;
    private final StudyCommentLikeService studyCommentLikeService;

    public StudyApiController(StudyService studyService,
                              StudyLikeService studyLikeService,
                              StudyCommentService studyCommentService,
                              StudyCommentLikeService studyCommentLikeService) {
        this.studyService = studyService;
        this.studyLikeService = studyLikeService;
        this.studyCommentService = studyCommentService;
        this.studyCommentLikeService = studyCommentLikeService;
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

    @PostMapping("/cancel/{id}")
    public Long cancel(@AuthenticationPrincipal UserAccount userAccount,
                       @PathVariable Long id) {
        return studyService.cancelStudy(userAccount, id);
    }

    @PostMapping("/like/{studyId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Long like(@AuthenticationPrincipal UserAccount userAccount,
                     @PathVariable Long studyId) {
        return studyLikeService.like(userAccount, studyId);
    }

    @DeleteMapping("/like/{studyId}")
    public Long unLike(@AuthenticationPrincipal UserAccount userAccount,
                       @PathVariable Long studyId) {
        return studyLikeService.unLike(userAccount, studyId);
    }

    @PostMapping("/{studyId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public StudyCommentResultDto createStudyComment(@AuthenticationPrincipal UserAccount userAccount,
                                                    @PathVariable Long studyId,
                                                    @RequestBody StudyCommentCreateDto studyCommentCreateDto) {
        return studyCommentService.createStudyComment(userAccount, studyId, studyCommentCreateDto);
    }

    @DeleteMapping("/comment/{studyCommentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Long deleteStudyComment(@AuthenticationPrincipal UserAccount userAccount,
                                   @PathVariable Long studyCommentId) {
        return studyCommentService.deleteStudyComment(userAccount, studyCommentId);
    }

    @PostMapping("/comment/{commentId}/like")
    @ResponseStatus(HttpStatus.CREATED)
    public Long likeComment(@AuthenticationPrincipal UserAccount userAccount,
                            @PathVariable Long commentId) {
        return studyCommentLikeService.likeComment(userAccount, commentId);
    }

    @DeleteMapping("/comment/{commentId}/unlike")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Long unLikeComment(@AuthenticationPrincipal UserAccount userAccount,
                              @PathVariable Long commentId) {
        return studyCommentLikeService.unlikeComment(userAccount, commentId);
    }
}
