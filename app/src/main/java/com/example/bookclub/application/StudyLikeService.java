package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyLike;
import com.example.bookclub.domain.StudyLikeRepository;
import com.example.bookclub.dto.StudyLikeResultDto;
import com.example.bookclub.errors.StudyLikeAlreadyExistedException;
import com.example.bookclub.errors.StudyLikeNotExistedException;
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
	 * 로그인한 사용자와 스터디 아이디로 스터디 좋아요를 생성하고 아이디를 반환한다.
	 *
	 * @param userAccount 로그인한 사용자
	 * @param studyId 스터디 아이디 식별자
	 * @return 생성된 스터디 좋아요 아이디
	 * @throws StudyLikeAlreadyExistedException 주어진 스터디 아이디에 해당하는 좋아요가 존재하는 경우
	 */
	@Transactional
	public StudyLikeResultDto like(UserAccount userAccount, Long studyId) {
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

		return StudyLikeResultDto.of(studyId);
	}

	/**
	 * 로그인한 사용자와 스터디 아이디로 스터디 좋아요를 제거하고 아이디를 반환한다.
	 *
	 * @param userAccount 로그인한 사용자
	 * @param studyId 스터디 아이디 식별자
	 * @return 삭제된 스터디 좋아요 아이디
	 * @throws StudyLikeNotExistedException 주어진 스터디 아이디에 해당하는 좋아요가 없는 경우
	 */
	@Transactional
	public StudyLikeResultDto unLike(UserAccount userAccount, Long studyId) {
		Long accountId = userAccount.getAccount().getId();

		Study study = studyService.getStudy(studyId);
		Account account = accountService.findAccount(accountId);
		StudyLike savedStudyLike = studyLikeRepository.findByStudyAndAccount(study, account)
				.orElseThrow(StudyLikeNotExistedException::new);

		studyLikeRepository.delete(savedStudyLike);

		return StudyLikeResultDto.of(savedStudyLike.getId());
	}
}
