package com.example.loginproject.service;

import com.example.loginproject.entity.User;
import com.example.loginproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;  // ✅ 여기만 남기기

    public void register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalStateException("이미 존재하는 아이디입니다.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));  // ✅ 깔끔하게 암호화
        userRepository.save(user);
    }

    public void update(User user) {
        userRepository.save(user);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}

