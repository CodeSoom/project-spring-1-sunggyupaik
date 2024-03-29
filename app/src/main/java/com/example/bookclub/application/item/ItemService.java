package com.example.bookclub.application.item;

import com.example.bookclub.dto.ItemDto;
import com.example.bookclub.infrastructure.item.JpaItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService {
	private final JpaItemRepository itemRepository;

	public ItemService(JpaItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	@Transactional(readOnly = true)
	public ItemDto.ItemResultDto detailItem(String title) {
		return itemRepository.findByTitle(title);
	}
}
