package com.seun.scheduler.service;

import com.seun.scheduler.domain.User;
import com.seun.scheduler.dto.*;
import com.seun.scheduler.repository.UserRepository;
import com.seun.scheduler.security.JwtUtil;
import com.seun.scheduler.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public String login(LoginRequest dto) {
        // 아이디 확인
        User user = userRepository.findByUserId(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        // 비밀번호 비교
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        user.updateLastLoginTime();
        return jwtUtil.createToken(user.getUserId());
    }

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
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

        return new UserDetailsImpl(user);
    }

    public ResponseEntity<CommonResponse<UserProfileResponse>> getUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

        UserProfileResponse userProfile = UserProfileResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .build();

        CommonResponse<UserProfileResponse> response = CommonResponse.<UserProfileResponse>builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK.name())
                .message("success")
                .data(userProfile)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Transactional
    public ResponseEntity<CommonResponse<UserProfileResponse>> updateUser(String userId, UpdateUserRequest request, MultipartFile image) throws IOException {
        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalArgumentException("유저를 찾을 수 없습니다.")
        );

        // 비밀번호 변경 시
        if (request.getPassword() != null) {
            if (!request.getPassword().equals(request.getPasswordConfirm())) {
                throw new IllegalArgumentException("비밀번호와 비밀번호 확인 값이 같지 않습니다.");
            }

            String encodePassword = passwordEncoder.encode(request.getPassword());

            user.updatePassword(encodePassword);
            user.updateUserProfile(request);
        }

        if (image != null && !image.isEmpty()) {

            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            String savePath = System.getProperty("user.dir") + "/src/main/resources/static/user/";

            if (user.getProfileImage() != null) {
                File file = new File(savePath + user.getProfileImage());
                boolean result = file.delete();
            }
            // file upload
            image.transferTo(new File(savePath + fileName));
            user.updateProfileImage(fileName);
        }

        user.updateUserProfile(request);

        UserProfileResponse userProfile = UserProfileResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .build();

        CommonResponse<UserProfileResponse> response = CommonResponse.<UserProfileResponse>builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK.name())
                .message("success")
                .data(userProfile)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
