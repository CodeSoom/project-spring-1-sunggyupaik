package com.example.bookclub.controller;

import com.example.bookclub.application.AccountService;
import com.example.bookclub.application.BookService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.BookType;
import com.example.bookclub.security.CurrentAccount;
import org.json.simple.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private final AccountService accountService;

    public BookController(BookService bookService,
                          AccountService accountService) {
        this.bookService = bookService;
        this.accountService = accountService;
    }

    @GetMapping("/bestseller")
    public String booksBestSellerLists(@CurrentAccount Account account, Model model) {
        checkTopMenu(account, model);

        return getBookList(model, BookType.BESTSELLER, "");
    }

    @GetMapping("/recommend")
    public String booksRecommendLists(@CurrentAccount Account account, Model model) {
        checkTopMenu(account, model);

        return getBookList(model, BookType.RECOMMEND, "");
    }

    @GetMapping("/new")
    public String booksNewLists(@CurrentAccount Account account, Model model) {
        checkTopMenu(account, model);

        return getBookList(model, BookType.NEW, "");
    }

    @GetMapping("/search")
    public String booksSearchLists(@CurrentAccount Account account,
                                   Model model, @RequestParam String keyword) {
        checkTopMenu(account, model);
        
        return getBookList(model, BookType.SEARCH, keyword);
    }

    private String getBookList(Model model, BookType bookType, String keyword) {
        JSONArray searchBooks = bookService.getBookLists(bookType, keyword);
        model.addAttribute("book", searchBooks);
        model.addAttribute("bookType", BookType.getTitleFrom(bookType));
        return "books/books-lists";
    }

    private void checkTopMenu(Account account, Model model) {
        account = accountService.findUserByEmail(account.getEmail());
        model.addAttribute("account", account);
        if (account.isMangerOf(account.getStudy())) {
            model.addAttribute("studyManager", account.getStudy());
        }

        if (account.isApplierOf(account.getStudy())) {
            model.addAttribute("studyApply", account.getStudy());
        }
    }
}
