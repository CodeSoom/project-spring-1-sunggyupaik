package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Favorite;
import com.example.bookclub.domain.FavoriteRepository;
import com.example.bookclub.domain.Study;
import com.example.bookclub.errors.StudyFavoriteAlreadyExistedException;
import com.example.bookclub.errors.StudyFavoriteNotExistedException;
import com.example.bookclub.security.UserAccount;
import org.springframework.stereotype.Service;

@Service
public class StudyFavoriteService {
	private FavoriteRepository favoriteRepository;
	private AccountService accountService;
	private StudyService studyService;

	public StudyFavoriteService(FavoriteRepository favoriteRepository,
								AccountService accountService,
								StudyService studyService) {
		this.favoriteRepository = favoriteRepository;
		this.accountService = accountService;
		this.studyService = studyService;
	}

	public Long favoriteStudy(UserAccount userAccount, Long studyId) {
		Long accountId = userAccount.getAccount().getId();

		Account account = accountService.findUser(accountId);
		Study study = studyService.getStudy(studyId);

		Favorite favorite = Favorite.builder()
				.account(account)
				.study(study)
				.build();

		favoriteRepository.findByStudyAndAccount(study, account)
				.ifPresent(studyFavorite -> {
					throw new StudyFavoriteAlreadyExistedException(studyFavorite.getId());
				});

		favorite.addStudyAndAccount(study, account);
		favoriteRepository.save(favorite);

		return studyId;
	}

	public Long unFavoriteStudy(UserAccount userAccount, Long studyId) {
		Long accountId = userAccount.getAccount().getId();

		Study study = studyService.getStudy(studyId);
		Account account = accountService.findUser(accountId);

		Favorite savedFavorite = favoriteRepository.findByStudyAndAccount(study, account)
				.orElseThrow(() -> new StudyFavoriteNotExistedException(studyId));

		favoriteRepository.delete(savedFavorite);

		return savedFavorite.getId();
	}
}
