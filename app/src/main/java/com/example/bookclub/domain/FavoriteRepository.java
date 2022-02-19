package com.example.bookclub.domain;

import java.util.Optional;

public interface FavoriteRepository {
	Favorite save(Favorite favorite);

	Optional<Favorite> findByStudyAndAccount(Study study, Account account);

	void delete(Favorite favorite);
}
