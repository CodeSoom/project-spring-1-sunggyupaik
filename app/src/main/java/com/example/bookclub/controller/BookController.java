package com.example.bookclub.controller;

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
 *  베스트셀러, 추천, 신간 기준으로 도서 검색 페이지를 요청한다.
 */
@Controller
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * 주어진 로그인한 사용자의 정보를 가지고 베스트셀러 도서 조회 페이지로 이동한다
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
     * 주어진 로그인한사용자 정보를 가지고 추천도서 조회 페이지로 이동한다
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
     * 주어진 로그인한사용자 정보를 가지고 신간도서 조회 페이지로 이동한다
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
     * 주어진 로그인한사용자 정보와 검색에 해당하는 책 조회 페이지로 이동한다
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
     * 주어진 로그인한사용자 정보를 가지고 검색어와 책 분류에 해당하는 책을 조회한다
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
