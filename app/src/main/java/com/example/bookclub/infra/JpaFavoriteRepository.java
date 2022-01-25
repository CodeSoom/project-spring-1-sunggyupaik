package com.example.bookclub.infra;

import com.example.bookclub.domain.Favorite;
import com.example.bookclub.domain.FavoriteRepository;
import org.springframework.data.repository.CrudRepository;

public interface JpaFavoriteRepository
		extends FavoriteRepository, CrudRepository<Favorite, Long> {
	Favorite save(Favorite favorite);
}
