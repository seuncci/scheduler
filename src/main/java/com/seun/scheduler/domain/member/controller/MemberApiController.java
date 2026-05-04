package com.seun.scheduler.domain.member.controller;

import com.seun.scheduler.domain.member.dto.MemberJoinRequest;
import com.seun.scheduler.domain.member.dto.MemberProfileResponse;
import com.seun.scheduler.domain.member.dto.MemberProfileUpdateRequest;
import com.seun.scheduler.domain.member.dto.NotificationSummaryResponse;
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

    @PatchMapping("/me")
    public CommonResponse<Void> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestPart(value = "data") MemberProfileUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile profileImage
            ) throws IOException {

        memberService.updateProfile(userDetails.getUsername(), request, profileImage);

        return CommonResponse.result(ResultCode.PROFILE_UPDATE_SUCCESS);
    }

    @GetMapping("/me/notifications/summary")
    public CommonResponse<NotificationSummaryResponse> getNotificationSummary(@AuthenticationPrincipal CustomUserDetails userDetails) {

        NotificationSummaryResponse notification = memberService.getNotificationSummary(userDetails.getUsername());

        return CommonResponse.result(ResultCode.NOTIFICATION_GET_SUCCESS, notification);
    }

    @PostMapping("/me/invitations/{invitationId}/accept")
    public CommonResponse<Void> acceptInvitation(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("invitationId") Long invitationId) {

        memberService.acceptInvitation(userDetails.getUsername(), invitationId);
        return CommonResponse.result(ResultCode.INVITE_ACCEPT_SUCCESS);
    }

    @PostMapping("/me/invitations/{invitationId}/reject")
    public CommonResponse<Void> rejectInvitation(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("invitationId") Long invitationId) {

        memberService.rejectInvitation(userDetails.getUsername(), invitationId);
        return CommonResponse.result(ResultCode.INVITE_REJECT_SUCCESS);
    }
}