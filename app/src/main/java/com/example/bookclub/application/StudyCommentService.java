package com.example.bookclub.application;

import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyComment;
import com.example.bookclub.domain.StudyCommentRepository;
import com.example.bookclub.dto.StudyCommentCreateDto;
import com.example.bookclub.dto.StudyCommentResultDto;
import com.example.bookclub.errors.StudyCommentContentNotExistedException;
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
	 * 주어진 스터디 식별자와 생성할 댓글 정보로 스터디 댓글을 생성하고 스터디 댓글 정보를 반환한다.
	 *
	 * @param userAccount 로그인한 사용자
	 * @param studyId 스터디 식별자
	 * @param studyCommentCreateDto 생성할 댓글 정보
	 * @return 생성된 댓글 정보
	 * @throws StudyCommentContentNotExistedException 생성할 댓글 정보가 비어있는 경우
	 */
	@Transactional
	public StudyCommentResultDto createStudyComment(UserAccount userAccount, Long studyId,
													StudyCommentCreateDto studyCommentCreateDto) {
		Study study = studyService.getStudy(studyId);
		if(studyCommentCreateDto.getContent().isBlank()) {
			throw new StudyCommentContentNotExistedException();
		}

		StudyComment studyComment = studyCommentCreateDto.toEntity(userAccount.getAccount(), study);

		StudyComment savedStudyComment = studyCommentRepository.save(studyComment);

		return StudyCommentResultDto.of(savedStudyComment, userAccount.getAccount());
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
	public StudyCommentResultDto deleteStudyComment(UserAccount userAccount, long studyCommentId) {
		StudyComment studyComment = getStudyComment(studyCommentId);
		if(!studyComment.getAccount().getId().equals(userAccount.getAccount().getId())) {
			throw new StudyCommentDeleteBadRequest();
		}

		studyCommentRepository.delete(studyComment);

		return StudyCommentResultDto.of(studyComment, userAccount.getAccount());
	}

	/**
	 * 주어진 스터디 댓글 식별자로 스터디 댓글을 반환한다.
	 *
	 * @param id 스터디 댓글 식별자
	 * @return 조회한 스터디 댓글
	 * @throws StudyCommentNotFoundException 주어진 스터디 댓글 식별자에 해당하는 스터디 댓글이 존재하지 않는 경우
	 */
	@Transactional
	public StudyComment getStudyComment(Long id) {
		return studyCommentRepository.findById(id)
				.orElseThrow(() -> new StudyCommentNotFoundException(id));
	}
}
