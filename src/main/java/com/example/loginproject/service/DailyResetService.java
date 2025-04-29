package com.example.loginproject.service;

import com.example.loginproject.entity.User;
import com.example.loginproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyResetService {

    private final UserRepository userRepository;

    // 매일 자정(00:00:00)에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void resetTodayStats() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            user.setTodayWins(0);
            user.setTodayLoses(0);
            user.setTodayPlays(0);
            user.setLastPlayedDate(LocalDate.now());
        }

        userRepository.saveAll(users);

        System.out.println("✅ [DailyResetService] 모든 유저의 오늘 기록 초기화 완료!");
    }
}