package com.example.bookclub.application;

import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.study.favorite.Favorite;
import com.example.bookclub.domain.study.favorite.FavoriteRepository;
import com.example.bookclub.domain.study.Study;
import com.example.bookclub.dto.StudyApiDto;
import com.example.bookclub.errors.StudyFavoriteAlreadyExistedException;
import com.example.bookclub.errors.StudyFavoriteNotExistedException;
import com.example.bookclub.errors.StudyNotFoundException;
import com.example.bookclub.security.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class StudyFavoriteServiceTest {
	private final static Long ACCOUNT_EXISTED_ID = 1L;
	private final static Long STUDY_EXISTED_ID = 2L;

	private final static Long STUDY_FAVORITE_CREATE_ID = 3L;
	private final static Long STUDY_NOT_EXISTED_ID = 999L;

	private Account account;
	private Study study;
	private UserAccount userAccount;
	private Favorite favorite;
	private Favorite createdFavorite;

	private FavoriteRepository favoriteRepository;
	private AccountService accountService;
	private StudyService studyService;
	private StudyFavoriteService studyFavoriteService;

	@BeforeEach
	void setUp() {
		favoriteRepository = mock(FavoriteRepository.class);
		accountService = mock(AccountService.class);
		studyService = mock(StudyService.class);

		studyFavoriteService = new StudyFavoriteService(
				favoriteRepository, accountService, studyService
		);

		account = Account.builder()
				.id(ACCOUNT_EXISTED_ID)
				.build();

		study = Study.builder()
				.id(STUDY_EXISTED_ID)
				.build();

		favorite = Favorite.builder()
				.account(account)
				.study(study)
				.build();

		createdFavorite = Favorite.builder()
				.id(STUDY_FAVORITE_CREATE_ID)
				.build();

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("USER"));

		userAccount = UserAccount.builder()
				.account(account)
				.authorities(authorities)
				.build();
	}

	@Test
	void createFavoriteStudyWithValidAttribute() {
		given(accountService.findAccount(ACCOUNT_EXISTED_ID)).willReturn(account);
		given(studyService.getStudy(STUDY_EXISTED_ID)).willReturn(study);
		given(favoriteRepository.save(any(Favorite.class))).willReturn(createdFavorite);

		StudyApiDto.StudyFavoriteResultDto studyFavoriteResultDto = studyFavoriteService.favoriteStudy(userAccount, STUDY_EXISTED_ID);

		assertThat(studyFavoriteResultDto.getId()).isEqualTo(STUDY_FAVORITE_CREATE_ID);
	}

	@Test
	void createFavoriteStudyWithAlreadyExisted() {
		given(accountService.findAccount(ACCOUNT_EXISTED_ID)).willReturn(account);
		given(studyService.getStudy(STUDY_EXISTED_ID)).willReturn(study);
		given(favoriteRepository.findByStudyAndAccount(study, account)).willReturn(Optional.of(favorite));

		assertThatThrownBy(
				() -> studyFavoriteService.favoriteStudy(
						userAccount, STUDY_EXISTED_ID)
		)
				.isInstanceOf(StudyFavoriteAlreadyExistedException.class);
	}

	@Test
	void createFavoriteStudyWithNotExistedStudyId() {
		given(accountService.findAccount(ACCOUNT_EXISTED_ID)).willReturn(account);
		given(studyService.getStudy(STUDY_NOT_EXISTED_ID)).willThrow(StudyNotFoundException.class);

		assertThatThrownBy(
				() -> studyFavoriteService.favoriteStudy(userAccount, STUDY_NOT_EXISTED_ID)
		)
				.isInstanceOf(StudyNotFoundException.class);
	}

	@Test
	void deleteFavoriteStudyWithExistedId() {
		given(accountService.findAccount(ACCOUNT_EXISTED_ID)).willReturn(account);
		given(studyService.getStudy(STUDY_EXISTED_ID)).willReturn(study);
		given(favoriteRepository.findByStudyAndAccount(any(Study.class), any(Account.class)))
				.willReturn(Optional.of(createdFavorite));

		StudyApiDto.StudyFavoriteResultDto studyFavoriteResultDto = studyFavoriteService.unFavoriteStudy(userAccount, STUDY_EXISTED_ID);

		assertThat(studyFavoriteResultDto.getId()).isEqualTo(STUDY_FAVORITE_CREATE_ID);
	}

	@Test
	void deleteFavoritesStudyWithNotExistedStudyId() {
		given(accountService.findAccount(ACCOUNT_EXISTED_ID)).willReturn(account);
		given(studyService.getStudy(STUDY_NOT_EXISTED_ID)).willThrow(StudyNotFoundException.class);

		assertThatThrownBy(
				() -> studyFavoriteService.unFavoriteStudy(
						userAccount, STUDY_NOT_EXISTED_ID)
		)
				.isInstanceOf(StudyNotFoundException.class);
	}

	@Test
	void deleteFavoritesStudyWithNotExistedFavoritesStudy() {
		given(accountService.findAccount(ACCOUNT_EXISTED_ID)).willReturn(account);
		given(studyService.getStudy(STUDY_EXISTED_ID)).willReturn(study);
		given(favoriteRepository.findByStudyAndAccount(any(Study.class), any(Account.class)))
				.willReturn(Optional.empty());

		assertThatThrownBy(
				() -> studyFavoriteService.unFavoriteStudy(
						userAccount, STUDY_EXISTED_ID)
		)
				.isInstanceOf(StudyFavoriteNotExistedException.class);
	}
}