package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Favorite;
import com.example.bookclub.domain.FavoriteRepository;
import com.example.bookclub.domain.Study;
import com.example.bookclub.errors.StudyFavoriteAlreadyExistedException;
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

		Long savedStudyFavoriteId = studyFavoriteService.favoriteStudy(userAccount, STUDY_EXISTED_ID);

		assertThat(savedStudyFavoriteId).isEqualTo(STUDY_FAVORITE_CREATE_ID);
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
}