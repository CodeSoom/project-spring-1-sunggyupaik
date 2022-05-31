package com.example.bookclub.domain.study.favorite;

import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.study.Study;

import java.util.Optional;

public interface FavoriteRepository {
	Favorite save(Favorite favorite);

	Optional<Favorite> findByStudyAndAccount(Study study, Account account);

	void delete(Favorite favorite);
}
