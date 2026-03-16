package com.seun.scheduler.service;

import com.seun.scheduler.domain.Member;
import com.seun.scheduler.domain.member.dto.MemberJoinRequest;
import com.seun.scheduler.dto.*;
import com.seun.scheduler.repository.MemberRepository;
import com.seun.scheduler.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public String join(MemberJoinRequest dto) {

        if (isUserIdDuplicated(dto.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        if (isEmailDuplicated(dto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodePassword = passwordEncoder.encode(dto.getPassword());

        Member member = Member.builder()
                .memberId(dto.getUserId())
                .password(encodePassword)
                .name(dto.getName())
                .email(dto.getEmail())
                .build();

        Member saveMember = memberRepository.save(member);
        return saveMember.getMemberId();
    }

    // 아이디 중복 체크
    public boolean isUserIdDuplicated(String userId) {
        return memberRepository.existsByMemberId(userId);
    }

    // 이메일 중복 체크
    public boolean isEmailDuplicated(String email) {
        return memberRepository.existsByEmail(email);
    }

    public ResponseEntity<CommonResponse<UserProfileResponse>> getUser(String userId) {
        Member member = memberRepository.findByMemberId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

        UserProfileResponse userProfile = UserProfileResponse.builder()
                .userId(member.getMemberId())
                .name(member.getName())
                .email(member.getEmail())
                .profileImage(member.getProfileImage())
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
        Member member = memberRepository.findByMemberId(userId).orElseThrow(
                () -> new IllegalArgumentException("유저를 찾을 수 없습니다.")
        );

        // 비밀번호 변경 시
        if (request.getPassword() != null) {
            if (!request.getPassword().equals(request.getPasswordConfirm())) {
                throw new IllegalArgumentException("비밀번호와 비밀번호 확인 값이 같지 않습니다.");
            }

            String encodePassword = passwordEncoder.encode(request.getPassword());

            member.updatePassword(encodePassword);
            member.updateUserProfile(request);
        }

        if (image != null && !image.isEmpty()) {

            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            String savePath = System.getProperty("user.dir") + "/src/main/resources/static/user/";

            if (member.getProfileImage() != null) {
                File file = new File(savePath + member.getProfileImage());
                boolean result = file.delete();
            }
            // file upload
            image.transferTo(new File(savePath + fileName));
            member.updateProfileImage(fileName);
        }

        member.updateUserProfile(request);

        UserProfileResponse userProfile = UserProfileResponse.builder()
                .userId(member.getMemberId())
                .name(member.getName())
                .email(member.getEmail())
                .profileImage(member.getProfileImage())
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
