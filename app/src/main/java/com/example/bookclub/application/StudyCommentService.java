package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyComment;
import com.example.bookclub.domain.StudyCommentRepository;
import com.example.bookclub.dto.StudyCommentCreateDto;
import com.example.bookclub.dto.StudyCommentResultDto;
import com.example.bookclub.errors.StudyCommentNotFoundException;
import com.example.bookclub.security.UserAccount;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class StudyCommentService {
	private final StudyCommentRepository studyCommentRepository;
	private final AccountService accountService;
	private final StudyService studyService;

	public StudyCommentService(StudyCommentRepository studyCommentRepository,
							   AccountService accountService,
							   StudyService studyService) {
		this.studyCommentRepository = studyCommentRepository;
		this.accountService = accountService;
		this.studyService = studyService;
	}

	@Transactional
	public StudyCommentResultDto createStudyComment(UserAccount userAccount, Long studyId,
													StudyCommentCreateDto studyCommentCreateDto) {
		Account account = accountService.findUser(userAccount.getAccount().getId());
		Study study = studyService.getStudy(studyId);
		StudyComment studyComment = studyCommentCreateDto.toEntity(account, study);

		StudyComment savedStudyComment = studyCommentRepository.save(studyComment);

		return StudyCommentResultDto.of(savedStudyComment, userAccount.getAccount());
	}

	@Transactional
	public Long deleteStudyComment(UserAccount userAccount, long studyCommentId) {
		StudyComment studyComment = getStudyComment(studyCommentId);

		studyCommentRepository.delete(studyComment);

		return studyCommentId;
	}

	@Transactional
	public StudyComment getStudyComment(Long id) {
		return studyCommentRepository.findById(id)
				.orElseThrow(() -> new StudyCommentNotFoundException(id));
	}

	@Transactional
	public List<StudyComment> findByStudyId(Long studyId) {
		return studyCommentRepository.findByStudyId(studyId);
	}
}
