package com.example.loginproject.controller;

import com.example.loginproject.entity.LoginUserDetails;
import com.example.loginproject.entity.User;
import com.example.loginproject.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.SecureRandom;
import java.time.LocalDate;

@Controller
public class GameController {

    private final UserRepository userRepository;
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

        User user = loginUserDetails.getUser();
        LocalDate today = LocalDate.now();

        // 날짜 변경 시 초기화
        if (user.getLastPlayedDate() == null || !user.getLastPlayedDate().isEqual(today)) {
            user.setTodayWins(0);
            user.setTodayLoses(0);
            user.setTodayPlays(0);
            user.setLastPlayedDate(today);
        }

        user.setTodayPlays(user.getTodayPlays() + 1);

        // 현재 승률 계산
        int todayGames = user.getTodayWins() + user.getTodayLoses();
        double winRate = (todayGames == 0) ? 0 : ((double) user.getTodayWins() / todayGames) * 100;

        // 컴퓨터 선택 조작: 승률 30% 미만이면 유리하게
        String computer;
        if (winRate < 25) {
            computer = switch (choice) {
                case "✌" -> "🖐"; // 유저 승
                case "✊" -> "✌"; // 유저 승
                case "🖐" -> "✊"; // 유저 승
                default -> getRandomChoice(); // 예외처리
            };
        } else {
            computer = getRandomChoice();
        }

        String result;
        if (choice.equals(computer)) {
            result = "비겼습니다!";
        } else if ((choice.equals("✌") && computer.equals("🖐")) ||
                (choice.equals("✊") && computer.equals("✌")) ||
                (choice.equals("🖐") && computer.equals("✊"))) {
            result = "이겼습니다!";
            user.setWins(user.getWins() + 1);
            user.setTodayWins(user.getTodayWins() + 1);
            user.setLastWinDate(today);
        } else {
            result = "졌습니다!";
            user.setTodayLoses(user.getTodayLoses() + 1);
        }

        userRepository.save(user);

        // 결과 전달
        redirectAttributes.addFlashAttribute("choice", choice);
        redirectAttributes.addFlashAttribute("computer", computer);
        redirectAttributes.addFlashAttribute("result", result);
        redirectAttributes.addFlashAttribute("todayPlays", user.getTodayPlays());

        int updatedGames = user.getTodayWins() + user.getTodayLoses();
        double updatedWinRate = (updatedGames == 0) ? 0 : ((double) user.getTodayWins() / updatedGames) * 100;
        redirectAttributes.addFlashAttribute("winRate", String.format("%.1f%%", updatedWinRate));

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
        int todayGames = user.getTodayWins() + user.getTodayLoses();
        double winRate = (todayGames == 0) ? 0 : ((double) user.getTodayWins() / todayGames) * 100;

        model.addAttribute("winRate", String.format("%.1f%%", winRate));
        model.addAttribute("username", loginUserDetails.getUsername());

        return "game-result";
    }

    private String getRandomChoice() {
        String[] choices = {"✌", "✊", "🖐"};
        return choices[random.nextInt(3)];
    }
}
