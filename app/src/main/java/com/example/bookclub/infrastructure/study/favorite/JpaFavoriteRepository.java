package com.example.bookclub.infrastructure.study.favorite;

import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.study.favorite.Favorite;
import com.example.bookclub.domain.study.favorite.FavoriteRepository;
import com.example.bookclub.domain.study.Study;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaFavoriteRepository
		extends FavoriteRepository, CrudRepository<Favorite, Long> {
	Favorite save(Favorite favorite);

	Optional<Favorite> findByStudyAndAccount(Study study, Account account);

	void delete(Favorite favorite);
}
