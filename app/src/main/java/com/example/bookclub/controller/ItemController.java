package com.example.bookclub.controller;

import com.example.bookclub.application.item.ItemService;
import com.example.bookclub.dto.ItemDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/items")
public class ItemController {
	private final ItemService itemService;

	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}

	public String itemDetail(Model model, @RequestParam String title) {
		ItemDto.ItemResultDto item = itemService.detailItem(title);
		model.addAttribute("item", item);

		return "items/items-detail";
	}
}
