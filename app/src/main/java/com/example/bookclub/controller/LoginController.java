package com.example.bookclub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 로그인 , 로그인 실패, 로그인 요청, 로그인 제한 화면으로 이동한다
 */
@Controller
public class LoginController {
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 로그인 실패 페이지로 이동한다
     *
     * @param model 모델
     * @return 로그인 실패 페이지
     */
    @GetMapping("/login-error")
    public String loginError(Model model){
        model.addAttribute("loginError", true);
        return "login";
    }

    /**
     * 로그인 요청 페이지로 이동한다
     *
     * @param model 모델
     * @return 로그인 요청 페이지
     */
    @GetMapping("/login-required")
    public String loginRequired(Model model){
        model.addAttribute("loginRequired", true);
        return "login";
    }

    /**
     * 로그인 접근 제한 페이지로 이동한다
     *
     * @return 로그인 접근 제한 페이지
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "exception/access-Denied";
    }
}
