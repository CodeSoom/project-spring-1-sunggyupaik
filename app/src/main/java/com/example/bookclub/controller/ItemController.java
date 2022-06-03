package com.example.bookclub.controller;

import com.example.bookclub.application.account.AccountService;
import com.example.bookclub.application.item.ItemService;
import com.example.bookclub.domain.account.Account;
import com.example.bookclub.dto.ItemDto;
import com.example.bookclub.security.UserAccount;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/items")
public class ItemController {
	private final ItemService itemService;
	private final AccountService accountService;

	public ItemController(ItemService itemService, AccountService accountService) {
		this.itemService = itemService;
		this.accountService = accountService;
	}

	@GetMapping
	public String itemDetail(@AuthenticationPrincipal UserAccount userAccount, Model model,
							 @RequestParam(name = "title") String title) {
		checkTopMenu(userAccount.getAccount(), model);

		ItemDto.ItemResultDto item = itemService.detailItem(title);
		model.addAttribute("item", item);

		return "items/items-detail";
	}

	private void checkTopMenu(Account account, Model model) {
		if (account.isMangerOf(account.getStudy())) {
			model.addAttribute("studyManager", account.getStudy());
		}

		if (account.isApplierOf(account.getStudy())) {
			model.addAttribute("studyApply", account.getStudy());
		}

		model.addAttribute("account", account);
	}
}
