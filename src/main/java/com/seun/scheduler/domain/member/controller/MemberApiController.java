package com.seun.scheduler.domain.member.controller;

import com.seun.scheduler.domain.member.dto.MemberJoinRequest;
import com.seun.scheduler.domain.member.dto.MemberProfileResponse;
import com.seun.scheduler.domain.member.dto.MemberProfileUpdateRequest;
import com.seun.scheduler.global.common.CommonResponse;
import com.seun.scheduler.global.common.ResultCode;
import com.seun.scheduler.domain.member.service.MemberService;
import com.seun.scheduler.security.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public CommonResponse<Void> join(@Valid @RequestBody MemberJoinRequest request) {

        memberService.join(request);

        return CommonResponse.result(ResultCode.SIGNUP_SUCCESS);
    }

    @GetMapping("/me")
    public CommonResponse<MemberProfileResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {

        MemberProfileResponse profile = memberService.getMyProfile(userDetails.getUsername());

        return CommonResponse.result(ResultCode.PROFILE_GET_SUCCESS, profile);
    }

    @PatchMapping("/me")
    public CommonResponse<Void> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestPart(value = "data") MemberProfileUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile profileImage
            ) throws IOException {

        memberService.updateProfile(userDetails.getUsername(), request, profileImage);

        return CommonResponse.result(ResultCode.PROFILE_UPDATE_SUCCESS);
    }

}