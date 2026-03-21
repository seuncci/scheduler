package com.seun.scheduler.domain.member.service;

import com.seun.scheduler.domain.Member;
import com.seun.scheduler.domain.member.dto.MemberJoinRequest;
import com.seun.scheduler.domain.member.dto.MemberProfileResponse;
import com.seun.scheduler.domain.member.dto.MemberProfileUpdateRequest;
import com.seun.scheduler.global.common.ResultCode;
import com.seun.scheduler.global.error.CustomException;
import com.seun.scheduler.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(MemberJoinRequest request) {

        // 입력값 검증
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new CustomException(ResultCode.PASSWORD_MISMATCH);
        }

        if (isMemberIdDuplicated(request.getMemberId())) {
            throw new CustomException(ResultCode.DUPLICATE_MEMBER_ID);
        }

        if (isEmailDuplicated(request.getEmail())) {
            throw new CustomException(ResultCode.DUPLICATE_EMAIL);
        }

        memberRepository.save(Member.of(request, passwordEncoder.encode(request.getPassword())));
    }

    public MemberProfileResponse getMyProfile(String memberId) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));

        return MemberProfileResponse.from(member);
    }

    @Transactional
    public void updateProfile(String memberId, MemberProfileUpdateRequest request, MultipartFile profileImage) throws IOException {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));

       // 비밀번호 변경 시
       if (request.getPassword() != null) {
           if (!request.getPassword().equals(request.getPasswordConfirm())) {
               throw new CustomException(ResultCode.PASSWORD_MISMATCH);
           }

           String encodePassword = passwordEncoder.encode(request.getPassword());

           member.updatePassword(encodePassword);
       }

       // 이메일을 변경했을 경우만 중복 체크
        if (!member.getEmail().equals(request.getEmail()) && isEmailDuplicated(request.getEmail())) {
            throw new CustomException(ResultCode.DUPLICATE_EMAIL);
        }

       member.updateProfile(request.getName(), request.getEmail());

       if (profileImage != null && !profileImage.isEmpty()) {

           String fileName = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();
           String savePath = System.getProperty("user.dir") + "/src/main/resources/static/member/";

           if (member.getProfileImage() != null) {

               Path path = Paths.get(savePath + member.getProfileImage());

               Files.deleteIfExists(path);
           }

           profileImage.transferTo(new File(savePath + fileName));

           member.updateProfileImage(fileName);
       }
    }

    // 아이디 중복 체크
    private boolean isMemberIdDuplicated(String memberId) {

        return memberRepository.existsByMemberId(memberId);
    }

    // 이메일 중복 체크
    private boolean isEmailDuplicated(String email) {

        return memberRepository.existsByEmail(email);
    }
}