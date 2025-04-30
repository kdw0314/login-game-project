package com.example.loginproject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users") // 👈 이렇게!
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String name;
    private String email;

    @Column(name = "wins", nullable = false)
    private Integer wins = 0;

    @Column(name = "last_win_date")
    private LocalDate lastWinDate;

    @Column(name = "today_wins")
    private int todayWins = 0;

    @Column(name = "today_loses") // ✅ 추가!
    private Integer todayLoses = 0;

    @Column(name = "today_plays")
    private int todayPlays = 0;

    @Column(name = "last_played_date")
    private LocalDate lastPlayedDate;
}
