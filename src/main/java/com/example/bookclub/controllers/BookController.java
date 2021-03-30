package com.example.bookclub.controllers;

import com.example.bookclub.application.BookService;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/books/bestseller")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String lists(Model model) throws IOException, ParseException {
        JSONArray bestSellers = bookService.getBestSellers();
        model.addAttribute("book", bestSellers);
        return "books/books-lists";
    }
}
