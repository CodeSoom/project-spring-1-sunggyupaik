package com.example.bookclub.application;

import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyComment;
import com.example.bookclub.domain.StudyCommentRepository;
import com.example.bookclub.dto.StudyCommentCreateDto;
import com.example.bookclub.dto.StudyCommentResultDto;
import com.example.bookclub.errors.StudyCommentDeleteBadRequest;
import com.example.bookclub.errors.StudyCommentNotFoundException;
import com.example.bookclub.security.UserAccount;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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
	 * 로그인한 사용자와 스터디 아이디, 생성할 댓글 정보로 댓글을 생성하고 댓글 정보를 반환한다.
	 *
	 * @param userAccount 로그인한 사용자
	 * @param studyId 스터디 아이디 식별자
	 * @param studyCommentCreateDto 생성할 댓글 정보
	 * @return 생성된 댓글 정보
	 */
	@Transactional
	public StudyCommentResultDto createStudyComment(UserAccount userAccount, Long studyId,
													StudyCommentCreateDto studyCommentCreateDto) {
		Study study = studyService.getStudy(studyId);
		StudyComment studyComment = studyCommentCreateDto.toEntity(userAccount.getAccount(), study);

		StudyComment savedStudyComment = studyCommentRepository.save(studyComment);

		return StudyCommentResultDto.of(savedStudyComment, userAccount.getAccount());
	}

	/**
	 * 로그인한 사용자와 스터디 댓글 아이디로 댓글을 삭제하고 아이디를 반환한다.
	 *
	 * @param userAccount 로그인한 사용자
	 * @param studyCommentId 삭제할 스터디 댓글 아이디 식별자
	 * @return 삭제된 스터디 댓글 아이디
	 * @throws StudyCommentDeleteBadRequest 스터디 댓글이 자신의 댓글이 아닌 경우
	 */
	@Transactional
	public Long deleteStudyComment(UserAccount userAccount, long studyCommentId) {
		StudyComment studyComment = getStudyComment(studyCommentId);
		if(!studyComment.getAccount().getId().equals(userAccount.getAccount().getId())) {
			throw new StudyCommentDeleteBadRequest();
		}

		studyCommentRepository.delete(studyComment);

		return studyCommentId;
	}

	/**
	 * 주어진 아이디로 스터디 댓글을 조회하고 반환한다.
	 *
	 * @param id 스터디 댓글 아이디 식별자
	 * @return 조회한 스터디 댓글
	 * @throws StudyCommentNotFoundException 주어진 아이디에 해당하는 스터디 댓글이 존재하지 않는 경우
	 */
	@Transactional
	public StudyComment getStudyComment(Long id) {
		return studyCommentRepository.findById(id)
				.orElseThrow(() -> new StudyCommentNotFoundException(id));
	}
}
