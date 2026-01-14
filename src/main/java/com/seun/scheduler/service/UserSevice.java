package com.seun.scheduler.service;

import com.seun.scheduler.domain.User;
import com.seun.scheduler.dto.UserJoinRequest;
import com.seun.scheduler.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSevice {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String join(UserJoinRequest dto) {
        if (isUserIdDuplicated(dto.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        if (isEmailDuplicated(dto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodePassword = passwordEncoder.encode(dto.getPassword());

        User user = User.builder()
                .userId(dto.getUserId())
                .password(encodePassword)
                .name(dto.getName())
                .email(dto.getEmail())
                .build();

        User saveUser = userRepository.save(user);
        return saveUser.getUserId();
    }

    // 아이디 중복 체크
    public boolean isUserIdDuplicated(String userId) {
        return userRepository.existsByUserId(userId);
    }

    // 이메일 중복 체크
    public boolean isEmailDuplicated(String email) {
        return userRepository.existsByUserId(email);
    }
}
