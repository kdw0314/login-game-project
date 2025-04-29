package com.example.loginproject.controller;

import com.example.loginproject.entity.User;
import com.example.loginproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.loginproject.entity.LoginUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.servlet.http.HttpServletResponse;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    /*@GetMapping("/")
    public String rootRedirect() {
        return "redirect:/login"; // 회원가입 대신 로그인으로 변경
    }*/
    @GetMapping("/signup")
    public String signupForm() {
        return "signup"; // signup.html
    }

    @PostMapping("/signup")
    public String signup(User user, Model model) {
        try {
            userService.register(user);
            return "redirect:/signup-success";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "signup"; // 다시 signup.html 보여주면서 오류 메시지 전달
        }
    }
    @GetMapping("/mypage")
    public String myPage(HttpServletResponse response, HttpServletRequest request, Model model, @AuthenticationPrincipal LoginUserDetails loginUserDetails) {
        System.out.println("로그인 유저 정보: " + loginUserDetails);
        // 👉 캐시 방지 헤더 설정
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("/game-result")) {
            return "redirect:/game"; // 또는 에러페이지 등으로 리다이렉트
        }
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        model.addAttribute("user", loginUserDetails.getUser());
        return "mypage";
    }

    @GetMapping("/signup-success")
    public String signupSuccess() {
        return "redirect:/login";

    }

    @GetMapping("/login")
    public String loginForm(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/game";
        }

        return "login";
    }

    @GetMapping("/profile-edit")
    public String editProfileForm(HttpServletResponse response, Model model, @AuthenticationPrincipal LoginUserDetails loginUserDetails) {
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        model.addAttribute("user", loginUserDetails.getUser());
        return "profile-edit";
    }

    @PostMapping("/profile-edit")
    public String updateProfile(@ModelAttribute("user") User updatedUser,
                                @RequestParam(value = "password", required = false) String newPassword,
                                @AuthenticationPrincipal LoginUserDetails loginUserDetails) {
        User currentUser = loginUserDetails.getUser();

        currentUser.setName(updatedUser.getName());
        currentUser.setEmail(updatedUser.getEmail());

        if (newPassword != null && !newPassword.isBlank()) {
            currentUser.setPassword(userService.encodePassword(newPassword));
        }

        userService.update(currentUser);

        return "redirect:/mypage";
    }
/*    @GetMapping("/game")
    public String gamePage(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        return "game"; // game.html로 이동
    }
*/
}
