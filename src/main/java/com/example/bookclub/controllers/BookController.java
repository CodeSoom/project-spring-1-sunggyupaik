package com.example.bookclub.controllers;

import com.example.bookclub.application.BookService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.BookType;
import com.example.bookclub.security.CurrentAccount;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/bestseller")
    public String booksBestSellerLists(@CurrentAccount Account account, Model model)
            throws IOException, ParseException {
        if(account != null) {
            model.addAttribute("account", account);
        }
        JSONArray bestSellers = bookService.getBookLists(BookType.BESTSELLER, "");
        model.addAttribute("book", bestSellers);
        model.addAttribute("bookType", BookType.getTitleFrom(BookType.BESTSELLER));
        return "books/books-lists";
    }

    @GetMapping("/recommend")
    public String booksRecommendLists(@CurrentAccount Account account, Model model)
            throws IOException, ParseException {
        if(account != null) {
            model.addAttribute("account", account);
        }
        JSONArray recommends = bookService.getBookLists(BookType.RECOMMEND, "");
        model.addAttribute("book", recommends);
        model.addAttribute("bookType", BookType.getTitleFrom(BookType.RECOMMEND));
        return "books/books-lists";
    }

    @GetMapping("/new")
    public String booksNewLists(@CurrentAccount Account account, Model model)
            throws IOException, ParseException {
        if(account != null) {
            model.addAttribute("account", account);
        }
        JSONArray newBooks = bookService.getBookLists(BookType.NEW, "");
        model.addAttribute("book", newBooks);
        model.addAttribute("bookType", BookType.getTitleFrom(BookType.NEW));
        return "books/books-lists";
    }

    @GetMapping("/search")
    public String booksSearchLists(@CurrentAccount Account account,
                                   Model model,
                                   @RequestParam String keyword)
            throws IOException, ParseException {
        if(account != null) {
            model.addAttribute("account", account);
        }
        JSONArray searchBooks = bookService.getBookLists(BookType.SEARCH, keyword);
        model.addAttribute("book", searchBooks);
        model.addAttribute("bookType", BookType.getTitleFrom(BookType.SEARCH));
        return "books/books-lists";
    }
}
