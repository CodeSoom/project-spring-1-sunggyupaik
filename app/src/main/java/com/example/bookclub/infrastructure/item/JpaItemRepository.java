package com.example.bookclub.infrastructure.item;

import com.example.bookclub.domain.Item.Item;
import com.example.bookclub.dto.ItemDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaItemRepository
		extends ItemRepositoryCustom, JpaRepository<Item, Long> {
	ItemDto.ItemResultDto findByTitle(String title);
}
