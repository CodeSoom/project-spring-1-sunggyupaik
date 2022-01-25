package com.example.bookclub.application;

import com.example.bookclub.domain.FavoriteRepository;
import org.springframework.stereotype.Service;

@Service
public class StudyFavoriteService {
	private FavoriteRepository favoriteRepository;

	public StudyFavoriteService(FavoriteRepository favoriteRepository) {
		this.favoriteRepository = favoriteRepository;
	}

	public Long favoriteStudy(Long id) {
		return null;
	}
}
