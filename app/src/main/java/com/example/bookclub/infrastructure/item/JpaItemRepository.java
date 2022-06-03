package com.example.bookclub.infrastructure.item;

import com.example.bookclub.domain.Item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaItemRepository
		extends ItemRepositoryCustom, JpaRepository<Item, Long> {
	Item findByTitle(String title);
}
