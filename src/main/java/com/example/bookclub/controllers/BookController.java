package com.example.bookclub.controllers;

import com.example.bookclub.application.BookService;
import com.example.bookclub.domain.BookType;
import com.example.bookclub.domain.StudyState;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/bestseller")
    public String booksBestSellerLists(Model model) throws IOException, ParseException {
        JSONArray bestSellers = bookService.getBookLists(BookType.BESTSELLER);
        model.addAttribute("book", bestSellers);
        model.addAttribute("bookType", BookType.getTitleFrom(BookType.BESTSELLER));
        return "books/books-lists";
    }

    @GetMapping("/recommend")
    public String booksRecommendLists(Model model) throws IOException, ParseException {
        JSONArray recommends = bookService.getBookLists(BookType.RECOMMEND);
        model.addAttribute("book", recommends);
        model.addAttribute("bookType", BookType.getTitleFrom(BookType.RECOMMEND));
        return "books/books-lists";
    }

    @GetMapping("/new")
    public String booksNewLists(Model model) throws IOException, ParseException {
        JSONArray newBooks = bookService.getBookLists(BookType.NEW);
        model.addAttribute("book", newBooks);
        model.addAttribute("bookType", BookType.getTitleFrom(BookType.NEW));
        return "books/books-lists";
    }
}
