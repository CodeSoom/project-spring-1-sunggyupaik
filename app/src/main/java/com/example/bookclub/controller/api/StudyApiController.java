package com.example.bookclub.controller.api;

import com.example.bookclub.application.StudyCommentLikeService;
import com.example.bookclub.application.StudyCommentService;
import com.example.bookclub.application.StudyFavoriteService;
import com.example.bookclub.application.StudyLikeService;
import com.example.bookclub.application.StudyService;
import com.example.bookclub.common.CommonResponse;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Study;
import com.example.bookclub.dto.StudyApplyResultDto;
import com.example.bookclub.dto.StudyCommentCreateDto;
import com.example.bookclub.dto.StudyCommentResultDto;
import com.example.bookclub.dto.StudyDto;
import com.example.bookclub.dto.StudyFavoriteResultDto;
import com.example.bookclub.dto.StudyLikeResultDto;
import com.example.bookclub.dto.StudyLikesCommentResultDto;
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
    private final StudyFavoriteService studyFavoriteService;

    public StudyApiController(StudyService studyService,
                              StudyLikeService studyLikeService,
                              StudyCommentService studyCommentService,
                              StudyCommentLikeService studyCommentLikeService,
                              StudyFavoriteService studyFavoriteService) {
        this.studyService = studyService;
        this.studyLikeService = studyLikeService;
        this.studyCommentService = studyCommentService;
        this.studyCommentLikeService = studyCommentLikeService;
        this.studyFavoriteService = studyFavoriteService;
    }

    /**
     * 스터디 리스트를 조회하고 반환한다
     *
     * @return 스터디 리스트
     */
    @GetMapping
    public CommonResponse<List<Study>> list() {
        List<Study> response = studyService.getStudies();
        return CommonResponse.success(response);
    }

    /**
     * 주어진 스터디 식별자에 대항하는 스터디를 조회하고 반환한다
     *
     * @param id 스터디 식별자
     * @return 조회한 스터디 정보
     */
    @GetMapping("/{id}")
    public CommonResponse<StudyDto.StudyResultDto> detail(@PathVariable Long id) {
        Study study = studyService.getStudy(id);
        StudyDto.StudyResultDto response = StudyDto.StudyResultDto.of(study);
        return CommonResponse.success(response);
    }

    /**
     * 주어진 로그인한 사용자, 생성할 스터디 정보로 스터디를 생성하고 반환한다
     *
     * @param account 로그인한 사용자
     * @param studyCreateDto 생성할 스터디 정보
     * @return 생성된 스터디 정보
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<StudyDto.StudyResultDto> create(
            @CurrentAccount Account account,
            @RequestBody StudyDto.StudyCreateDto studyCreateDto
    ) {
        StudyDto.StudyResultDto response = studyService.createStudy(account.getEmail(), studyCreateDto);
        return CommonResponse.success(response);
    }

    /**
     * 주어진 로그인한 사용자, 스터디 식별자에 해당하는 스터디를 수정할 정보로 수정하고 반환한다
     *
     * @param userAccount 로그인한 사용자
     * @param id 스터디 식별자
     * @param studyUpdateDto 수정할 스터디 정보
     * @return 수정된 스터디 정보
     */
    //@PreAuthorize("@studyManagerCheck.isManagerOfStudy(#userAccount.account)")
    @PatchMapping("/{id}")
    public CommonResponse<StudyDto.StudyResultDto> update(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable Long id,
            @RequestBody StudyDto.StudyUpdateDto studyUpdateDto
    ) {
        StudyDto.StudyResultDto response =
                studyService.updateStudy(userAccount.getAccount().getEmail(), id, studyUpdateDto);
        return CommonResponse.success(response);
    }

    /**
     * 주어진 로그인한 사용자, 스터디 식별자에 해당하는 스터디를 삭제하고 해당 스터디를 반환한다
     *
     * @param userAccount 로그인한 사용자
     * @param id 스터디 식별자
     * @return 삭제된 스터디 정보
     */
    //@PreAuthorize("@studyManagerCheck.isManagerOfStudy(#userAccount.account)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse<StudyDto.StudyResultDto> delete(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable Long id
    ) {
        StudyDto.StudyResultDto response = studyService.deleteStudy(userAccount.getAccount().getEmail(), id);
        return CommonResponse.success(response);
    }

    /**
     * 주어진 로그인한 사용자, 스터디 식별자에 해당하는 스터디 지원을 생성하고 스터디 식별자 정보를 반환한다
     *
     * @param userAccount 로그인한 사용자
     * @param id 스터디 식별자
     * @return 스터디 식별자 정보
     */
    @PostMapping("/apply/{id}")
    public CommonResponse<StudyApplyResultDto> apply(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable Long id
    ) {
        StudyApplyResultDto response = studyService.applyStudy(userAccount, id);
        return CommonResponse.success(response);
    }

    /**
     * 주어진 로그인한 사용자, 스터디 식별자에 해당하는 스터디 지원을 삭제하고 스터디 식별자 정보를 반환한다
     *
     * @param userAccount 로그인한 사용자
     * @param id 스터디 식별자
     * @return 스터디 식별자 정보
     */
    @PostMapping("/cancel/{id}")
    public CommonResponse<StudyApplyResultDto> cancel(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable Long id
    ) {
        StudyApplyResultDto response = studyService.cancelStudy(userAccount, id);
        return CommonResponse.success(response);
    }

    /**
     * 주어진 로그인한 사용자와 스터디 식별자에 해당하는 스터디 좋아요를 생성하고 스터디 식별자 정보를 반환한다
     *
     * @param userAccount 로그인한 사용자
     * @param studyId 스터디 식별자
     * @return 스터디 식별자 정보
     */
    @PostMapping("/like/{studyId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<StudyLikeResultDto> like(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable Long studyId
    ) {
        StudyLikeResultDto response = studyLikeService.like(userAccount, studyId);
        return CommonResponse.success(response);
    }

    /**
     * 주어진 로긍니한 사용자와 스터디 식별자에 해당하는 스터디 좋아요를 삭제하고 스터디 식별자 정보를 반환한다
     *
     * @param userAccount 로그인한 사용자
     * @param studyId 스터디 식별자
     * @return 스터디 식별자 정보
     */
    @DeleteMapping("/like/{studyId}")
    public CommonResponse<StudyLikeResultDto> unLike(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable Long studyId
    ) {
        StudyLikeResultDto response = studyLikeService.unLike(userAccount, studyId);
        return CommonResponse.success(response);
    }

    /**
     * 주어진 로그인한 사용자, 스터디 식별자, 생성할 댓글 정보로 스터디 댓글을 생성하고 반환한다
     *
     * @param userAccount 로그인한 사용자
     * @param studyId 스더티 식별자
     * @param studyCommentCreateDto 생성할 스터디 댓글 정보
     * @return 생성된 스터디 댓글 정보
     */
    @PostMapping("/{studyId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<StudyCommentResultDto> createStudyComment(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable Long studyId,
            @RequestBody StudyCommentCreateDto studyCommentCreateDto
    ) {
        StudyCommentResultDto response = studyCommentService
                .createStudyComment(userAccount, studyId, studyCommentCreateDto);
        return CommonResponse.success(response);
    }

    /**
     * 주어진 로그인한 사용자, 스터디 댓글 식별자에 해당하는 스터디 댓글을 삭제하고 반환한다
     *
     * @param userAccount 로그인한 사용자
     * @param studyCommentId 스터디 댓글 식별자
     * @return 삭제된 스터디 댓글 정보
     */
    @DeleteMapping("/comment/{studyCommentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse<StudyCommentResultDto> deleteStudyComment(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable Long studyCommentId
    ) {
        StudyCommentResultDto response = studyCommentService.deleteStudyComment(userAccount, studyCommentId);
        return CommonResponse.success(response);
    }

    /**
     * 주어진 로그인한 사용자, 스터디 댓글 식별자에 해당하는 스터디 댓글에 좋아요를 생성하고
     * 스터디 댓글 식별자 정보를 반환한다
     *
     * @param userAccount 로그인한 사용자
     * @param commentId 스터디 댓글 식별자
     * @return 스터디 댓글 식별자 정보
     */
    @PostMapping("/comment/{commentId}/like")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<StudyLikesCommentResultDto> likeComment(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable Long commentId
    ) {
        StudyLikesCommentResultDto response = studyCommentLikeService.likeComment(userAccount, commentId);
        return CommonResponse.success(response);
    }

    /**
     * 주어진 로그인한 사용자, 스터디 댓글 식별자에 해당하는 스터디 댓글에 좋아요를 삭제하고
     * 스터디 댓글 식별자 정보를 반환한다
     *
     * @param userAccount 로그인한 사용자
     * @param commentId 스터디 댓글 식별자
     * @return 스터디 댓글 식별자 정보
     */
    @DeleteMapping("/comment/{commentId}/unlike")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse<StudyLikesCommentResultDto> unLikeComment(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable Long commentId
    ) {
        StudyLikesCommentResultDto response = studyCommentLikeService.unlikeComment(userAccount, commentId);
        return CommonResponse.success(response);
    }

    /**
     * 주어진 로그인한 사용자, 스터디 식별자에 해당하는 스터디 즐겨찾기를 생성하고 스터디 식별자 정보를 반환한다
     *
     * @param userAccount 로그인한 사용자
     * @param id 스터디 식별자
     * @return 스터디 식별자 정보
     */
    @PostMapping("/{id}/favorite")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<StudyFavoriteResultDto> favoriteStudy(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable Long id
    ) {
        StudyFavoriteResultDto response = studyFavoriteService.favoriteStudy(userAccount, id);
        return CommonResponse.success(response);
    }

    /**
     * 주어진 로그인한 사용자, 스터디 식별자에 해당하는 스터디 즐겨찾기를 삭제하고 스터디 식별자 정보를 반환한다
     *
     * @param userAccount 로그인한 사용자
     * @param id 스터디 식별자
     * @return 스터디 식별자 정보
     */
    @DeleteMapping("/{id}/favorite")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse<StudyFavoriteResultDto> unFavoriteStudy(
            @AuthenticationPrincipal UserAccount userAccount,
            @PathVariable Long id
    ) {
        StudyFavoriteResultDto response = studyFavoriteService.unFavoriteStudy(userAccount, id);
        return CommonResponse.success(response);
    }
}
