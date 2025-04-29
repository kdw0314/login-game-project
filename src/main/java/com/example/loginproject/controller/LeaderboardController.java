package com.example.loginproject.controller;

import com.example.loginproject.entity.User;
import com.example.loginproject.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.loginproject.entity.LoginUserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class LeaderboardController {

    private final UserRepository userRepository;

    @GetMapping("/leaderboard")
    public String showLeaderboard(@AuthenticationPrincipal LoginUserDetails loginUserDetails,
                                  HttpServletRequest request,
                                  Model model) {
        String referer = request.getHeader("Referer");

        if (referer != null && !referer.contains("/game")) {
            return "redirect:/login";
        }

        model.addAttribute("username", loginUserDetails.getUsername());

        List<User> users = userRepository.findTop10ByOrderByWinsDesc();

        List<Map<String, Object>> topUsers = new ArrayList<>();
        for (User user : users) {
            Map<String, Object> map = new HashMap<>();
            map.put("username", user.getUsername());
            map.put("wins", user.getWins());

            int todayGames = user.getTodayWins() + user.getTodayLoses();
            double winRate = (todayGames == 0) ? 0 : ((double) user.getTodayWins() / todayGames) * 100;
            map.put("winRate", String.format("%.1f", winRate)); // 소수점 1자리까지

            topUsers.add(map);
        }

        model.addAttribute("topUsers", topUsers);

        return "leaderboard";
    }


}
