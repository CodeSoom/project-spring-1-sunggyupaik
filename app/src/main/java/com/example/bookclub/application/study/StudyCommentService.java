package com.example.bookclub.application.study;

import com.example.bookclub.common.exception.study.studycomment.StudyCommentContentNotExistedException;
import com.example.bookclub.common.exception.study.studycomment.StudyCommentDeleteBadRequest;
import com.example.bookclub.common.exception.study.studycomment.StudyCommentNotFoundException;
import com.example.bookclub.domain.study.Study;
import com.example.bookclub.domain.study.studycomment.StudyComment;
import com.example.bookclub.domain.study.studycomment.StudyCommentRepository;
import com.example.bookclub.dto.StudyApiDto;
import com.example.bookclub.security.UserAccount;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 스터디 댓글 생성, 삭제, 조회한다.
 */
@Service
public class StudyCommentService {
	private final StudyCommentRepository studyCommentRepository;
	private final StudyService studyService;

	public StudyCommentService(StudyCommentRepository studyCommentRepository,
							   StudyService studyService) {
		this.studyCommentRepository = studyCommentRepository;
		this.studyService = studyService;
	}

	/**
	 * 주어진 스터디 식별자와 생성할 댓글 정보로 스터디 댓글을 생성하고 스터디 댓글 정보를 반환한다.
	 *
	 * @param userAccount 로그인한 사용자
	 * @param studyId 스터디 식별자
	 * @param studyCommentCreateDto 생성할 댓글 정보
	 * @return 생성된 댓글 정보
	 * @throws StudyCommentContentNotExistedException 생성할 댓글 정보가 비어있는 경우
	 */
	@Transactional
	public StudyApiDto.StudyCommentResultDto createStudyComment(UserAccount userAccount, Long studyId,
																StudyApiDto.StudyCommentCreateDto studyCommentCreateDto) {
		Study study = studyService.getStudy(studyId);
		if(studyCommentCreateDto.getContent().isBlank()) {
			throw new StudyCommentContentNotExistedException();
		}

		StudyComment studyComment = studyCommentCreateDto.toEntity(userAccount.getAccount(), study);

		StudyComment savedStudyComment = studyCommentRepository.save(studyComment);

		return StudyApiDto.StudyCommentResultDto.of(savedStudyComment, userAccount.getAccount());
	}

	/**
	 * 주어진 스터디 댓글 식별자로 스터디 댓글을 삭제하고 스터디 댓글 식별자를 반환한다.
	 *
	 * @param userAccount 로그인한 사용자
	 * @param studyCommentId 스터디 댓글 식별자
	 * @return 삭제된 스터디 댓글 아이디
	 * @throws StudyCommentDeleteBadRequest 스터디 댓글이 로그인한 사용자의 댓글이 아닌 경우
	 */
	@Transactional
	public StudyApiDto.StudyCommentResultDto deleteStudyComment(UserAccount userAccount, long studyCommentId) {
		StudyComment studyComment = getStudyComment(studyCommentId);
		if(!studyComment.getAccount().getId().equals(userAccount.getAccount().getId())) {
			throw new StudyCommentDeleteBadRequest();
		}

		studyCommentRepository.delete(studyComment);

		return StudyApiDto.StudyCommentResultDto.of(studyComment, userAccount.getAccount());
	}

	/**
	 * 주어진 스터디 댓글 식별자로 스터디 댓글을 반환한다.
	 *
	 * @param id 스터디 댓글 식별자
	 * @return 조회한 스터디 댓글
	 * @throws StudyCommentNotFoundException 주어진 스터디 댓글 식별자에 해당하는 스터디 댓글이 존재하지 않는 경우
	 */
	@Transactional(readOnly = true)
	public StudyComment getStudyComment(Long id) {
		return studyCommentRepository.findById(id)
				.orElseThrow(() -> new StudyCommentNotFoundException(id));
	}
}
