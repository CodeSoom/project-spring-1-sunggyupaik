package com.example.bookclub.application.study;

import com.example.bookclub.application.account.AccountService;
import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.study.Study;
import com.example.bookclub.domain.study.studylike.StudyLike;
import com.example.bookclub.domain.study.studylike.StudyLikeRepository;
import com.example.bookclub.dto.StudyApiDto;
import com.example.bookclub.common.exception.study.studylike.StudyLikeAlreadyExistedException;
import com.example.bookclub.common.exception.study.studylike.StudyLikeNotExistedException;
import com.example.bookclub.security.UserAccount;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * 스터디 좋아요 생성, 삭제를 한다.
 *
 */
@Service
public class StudyLikeService {
	private final StudyService studyService;
	private final AccountService accountService;
	private final StudyLikeRepository studyLikeRepository;

	public StudyLikeService(StudyService studyService,
							AccountService accountService,
							StudyLikeRepository studyLikeRepository) {
		this.studyService = studyService;
		this.accountService = accountService;
		this.studyLikeRepository = studyLikeRepository;
	}

	/**
	 * 주어진 로그인한 사용자와 스터디 식별자에 해당하는 스터디 좋아요를 생성하고 스터디 식별자를 반환한다
	 *
	 * @param userAccount 로그인한 사용자
	 * @param studyId 스터디 식별자
	 * @return 생성된 스터디 좋아요 아이디
	 * @throws StudyLikeAlreadyExistedException 주어진 스터디 식별자에 해당하는 좋아요가 존재하는 경우
	 */
	@Transactional
	public StudyApiDto.StudyLikeResultDto like(UserAccount userAccount, Long studyId) {
		Long accountId = userAccount.getAccount().getId();

		Study study = studyService.getStudy(studyId);
		Account account = accountService.findAccount(accountId);
		StudyLike studyLike = StudyLike.builder()
				.study(study)
				.account(account)
				.build();

		studyLikeRepository.findByStudyAndAccount(study, account)
				.ifPresent(like -> {
					throw new StudyLikeAlreadyExistedException();
				});

		studyLike.addStudyAndAccount(study, account);
		studyLikeRepository.save(studyLike);

		return StudyApiDto.StudyLikeResultDto.of(studyId);
	}

	/**
	 * 주어진 로그인한 사용자와 스터디 식별자에 해당하는 스터디 좋아요를 삭제하고 스터디 식별자를 반환한다.
	 *
	 * @param userAccount 로그인한 사용자
	 * @param studyId 스터디 식별자
	 * @return 삭제된 스터디 좋아요 식별자
	 * @throws StudyLikeNotExistedException 주어진 스터디 식별자에 해당하는 좋아요가 없는 경우
	 */
	@Transactional
	public StudyApiDto.StudyLikeResultDto unLike(UserAccount userAccount, Long studyId) {
		Long accountId = userAccount.getAccount().getId();

		Study study = studyService.getStudy(studyId);
		Account account = accountService.findAccount(accountId);
		StudyLike savedStudyLike = studyLikeRepository.findByStudyAndAccount(study, account)
				.orElseThrow(StudyLikeNotExistedException::new);

		studyLikeRepository.delete(savedStudyLike);

		return StudyApiDto.StudyLikeResultDto.of(savedStudyLike.getId());
	}
}
