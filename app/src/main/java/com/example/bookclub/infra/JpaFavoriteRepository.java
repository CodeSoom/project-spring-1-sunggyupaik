package com.example.bookclub.infra;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Favorite;
import com.example.bookclub.domain.FavoriteRepository;
import com.example.bookclub.domain.Study;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaFavoriteRepository
		extends FavoriteRepository, CrudRepository<Favorite, Long> {
	Favorite save(Favorite favorite);

	Optional<Favorite> findByStudyAndAccount(Study study, Account account);
}
