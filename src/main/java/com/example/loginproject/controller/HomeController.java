package com.example.loginproject.controller;

import com.example.loginproject.entity.LoginUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal LoginUserDetails userDetails) {
        if (userDetails != null) {
            return "redirect:/game"; // ✅ 로그인 상태면 게임 페이지로
        }
        return "redirect:/login";   // ❌ 비로그인 상태면 로그인 페이지로
    }
}

