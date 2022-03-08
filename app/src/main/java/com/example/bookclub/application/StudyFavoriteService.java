package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Favorite;
import com.example.bookclub.domain.FavoriteRepository;
import com.example.bookclub.domain.Study;
import com.example.bookclub.errors.StudyFavoriteAlreadyExistedException;
import com.example.bookclub.errors.StudyFavoriteNotExistedException;
import com.example.bookclub.security.UserAccount;
import org.springframework.stereotype.Service;

/**
 * 스터디 좋아요  생성, 삭제를 한다.
 */
@Service
public class StudyFavoriteService {
	private final FavoriteRepository favoriteRepository;
	private final AccountService accountService;
	private final StudyService studyService;

	public StudyFavoriteService(FavoriteRepository favoriteRepository,
								AccountService accountService,
								StudyService studyService) {
		this.favoriteRepository = favoriteRepository;
		this.accountService = accountService;
		this.studyService = studyService;
	}

	/**
	 * 로그인한 사용자와 스터디 아이디로 스터디 좋아요를 생성하고 아이디를 반환한다.
	 *
	 * @param userAccount 로그인한 사용자
	 * @param studyId 스터디 아이디 식별자
	 * @return 생성된 스터디 좋아요 아이디
	 * @throws StudyFavoriteAlreadyExistedException 주어진 스터디 아이디에 해당하는 좋아요가 존재하는 경우
	 */
	public Long favoriteStudy(UserAccount userAccount, Long studyId) {
		Long accountId = userAccount.getAccount().getId();

		Account account = accountService.findAccount(accountId);
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

	/**
	 *
	 *
	 * @param userAccount 로그인한 사용자
	 * @param studyId 스터디 아이디 식별자
	 * @return 삭제된 스터디 좋아요 아이디
	 * @throws StudyFavoriteNotExistedException 주어진 스터디 아이디에 해당하는 좋아요가 없는 경우
	 */
	public Long unFavoriteStudy(UserAccount userAccount, Long studyId) {
		Long accountId = userAccount.getAccount().getId();

		Study study = studyService.getStudy(studyId);
		Account account = accountService.findAccount(accountId);

		Favorite savedFavorite = favoriteRepository.findByStudyAndAccount(study, account)
				.orElseThrow(() -> new StudyFavoriteNotExistedException(studyId));

		favoriteRepository.delete(savedFavorite);

		return savedFavorite.getId();
	}
}
