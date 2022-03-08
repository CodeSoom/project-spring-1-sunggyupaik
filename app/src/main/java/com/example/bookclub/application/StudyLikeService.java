package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyLike;
import com.example.bookclub.domain.StudyLikeRepository;
import com.example.bookclub.errors.StudyLikeAlreadyExistedException;
import com.example.bookclub.errors.StudyLikeNotExistedException;
import com.example.bookclub.security.UserAccount;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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

	@Transactional
	public Long like(UserAccount userAccount, Long studyId) {
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

		return studyId;
	}

	@Transactional
	public Long unLike(UserAccount userAccount, Long studyId) {
		Long accountId = userAccount.getAccount().getId();

		Study study = studyService.getStudy(studyId);
		Account account = accountService.findAccount(accountId);
		StudyLike savedStudyLike = studyLikeRepository.findByStudyAndAccount(study, account)
				.orElseThrow(StudyLikeNotExistedException::new);

		studyLikeRepository.delete(savedStudyLike);

		return savedStudyLike.getId();
	}
}
