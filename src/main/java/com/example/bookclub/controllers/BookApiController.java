package com.example.bookclub.controllers;

import com.example.bookclub.application.BookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/book")
public class BookApiController {
    private BookService bookService;

    public BookApiController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/bestseller")
    public String lists() throws IOException {
        return bookService.getBestSellers();
    }
}
