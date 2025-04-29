package com.example.loginproject.controller;

import com.example.loginproject.entity.LoginUserDetails;
import com.example.loginproject.entity.User;
import com.example.loginproject.repository.UserRepository;

import java.time.LocalDate;
import java.util.Random;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.SecureRandom;
@Controller
public class GameController {

    private final UserRepository userRepository;
 //   private final Random random = new Random();
    private final SecureRandom random = new SecureRandom();
    public GameController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/game")
    public String gamePage(@AuthenticationPrincipal LoginUserDetails userDetails, Model model) {
        model.addAttribute("username", userDetails.getUsername());
        return "game";
    }

    @PostMapping("/game")
    public String playGame(@RequestParam String choice,
                           @AuthenticationPrincipal LoginUserDetails loginUserDetails,
                           RedirectAttributes redirectAttributes) {
        String[] choices = {"✌", "✊", "🖐"};
        String computer = choices[random.nextInt(3)];
        String result;

        User user = loginUserDetails.getUser();
        LocalDate today = LocalDate.now();

        // 날짜가 다르면 초기화
        if (user.getLastPlayedDate() == null || !user.getLastPlayedDate().isEqual(today)) {
            user.setTodayWins(0);
            user.setTodayLoses(0);  // ✅ 오늘 진 것도 초기화
            user.setTodayPlays(0);
            user.setLastPlayedDate(today);
        }

        user.setTodayPlays(user.getTodayPlays() + 1); // (plays는 그냥 전체 게임수로 계속 올려둠)

        if (choice.equals(computer)) {
            result = "비겼습니다!";
            // 비기면 todayWins, todayLoses 아무것도 증가 안 함
        } else if ((choice.equals("✌") && computer.equals("🖐")) ||
                (choice.equals("✊") && computer.equals("✌")) ||
                (choice.equals("🖐") && computer.equals("✊"))) {
            result = "이겼습니다!";
            user.setWins(user.getWins() + 1);
            user.setTodayWins(user.getTodayWins() + 1);
            user.setLastWinDate(LocalDate.now());
        } else {
            result = "졌습니다!";
            user.setTodayLoses(user.getTodayLoses() + 1); // ✅ 졌을 때 todayLoses 증가
        }

        userRepository.save(user);

        redirectAttributes.addFlashAttribute("choice", choice);
        redirectAttributes.addFlashAttribute("computer", computer);
        redirectAttributes.addFlashAttribute("result", result);
        redirectAttributes.addFlashAttribute("todayPlays", user.getTodayPlays());
        // 👉 승률 계산 (비긴 건 제외)
        int todayGames = user.getTodayWins() + user.getTodayLoses();
        double winRate = (todayGames == 0) ? 0 : ((double) user.getTodayWins() / todayGames) * 100;
        redirectAttributes.addFlashAttribute("winRate", String.format("%.1f%%", winRate));

        return "redirect:/game-result";
    }

    @GetMapping("/game-result")
    public String showGameResult(@AuthenticationPrincipal LoginUserDetails loginUserDetails,
                                 HttpServletRequest request, Model model) {

        String referer = request.getHeader("Referer");

        if (!model.containsAttribute("choice") || !model.containsAttribute("result")) {
            return "redirect:/game";
        }

        if (referer != null && referer.contains("/leaderboard")) {
            return "redirect:/mypage";
        }

        User user = loginUserDetails.getUser();

        // 👉 승률 계산 (여기도 비긴 건 제외)
        int todayGames = user.getTodayWins() + user.getTodayLoses();
        double winRate = (todayGames == 0) ? 0 : ((double) user.getTodayWins() / todayGames) * 100;

        model.addAttribute("winRate", String.format("%.1f%%", winRate));
        model.addAttribute("username", loginUserDetails.getUsername());

        System.out.println("오늘 승률: " + winRate);

        return "game-result";
    }
}
