package com.example.bookclub.controller;

import com.example.bookclub.application.AccountService;
import com.example.bookclub.application.BookService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.BookType;
import com.example.bookclub.security.UserAccount;
import org.json.simple.JSONArray;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *  책을 베스트셀러, 추천, 신간 기준으로 검색한다
 */
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

    /**
     * 베스트셀러 도서 조회 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param model 모델
     * @return 베스트셀러 도서 조회 페이지
     */
    @GetMapping("/bestseller")
    public String booksBestSellerLists(@AuthenticationPrincipal UserAccount userAccount, Model model) {
        checkTopMenu(userAccount.getAccount(), model);

        return getBookList(model, BookType.BESTSELLER, "");
    }

    /**
     * 추천도서 조회 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param model 모델
     * @return 추천도서 조회 페이지
     */
    @GetMapping("/recommend")
    public String booksRecommendLists(@AuthenticationPrincipal UserAccount userAccount, Model model) {
        checkTopMenu(userAccount.getAccount(), model);

        return getBookList(model, BookType.RECOMMEND, "");
    }

    /**
     * 신간도서 조회 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param model 모델
     * @return 신간도서 조회 페이지
     */
    @GetMapping("/new")
    public String booksNewLists(@AuthenticationPrincipal UserAccount userAccount, Model model) {
        checkTopMenu(userAccount.getAccount(), model);

        return getBookList(model, BookType.NEW, "");
    }

    /**
     * 검색한 책 조회 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param model 모델
     * @return 검색한 책 조회 페이지
     */
    @GetMapping("/search")
    public String booksSearchLists(@AuthenticationPrincipal UserAccount userAccount,
                                   Model model, @RequestParam String search) {
        checkTopMenu(userAccount.getAccount(), model);
        
        return getBookList(model, BookType.SEARCH, search);
    }

    /**
     * 책 분류와 검색어에 따라 책을 조회한다
     *
     * @param model 모델
     * @param bookType 검색 분류(베스트셀러, 추천, 신간)
     * @param search 검색어
     * @return 검색한 책 조회 페이지
     */
    private String getBookList(Model model, BookType bookType, String search) {
        JSONArray searchBooks = bookService.getBookLists(bookType, search);
        model.addAttribute("book", searchBooks);
        model.addAttribute("bookType", BookType.getTitleFrom(bookType));
        return "books/books-lists";
    }

    /**
     * 로그인한 사용자의 스터디 개설, 참여 여부를 확인한다
     *
     * @param account 로그인한 사용자
     * @param model 모델
     */
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
