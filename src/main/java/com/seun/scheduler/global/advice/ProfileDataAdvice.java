package com.seun.scheduler.global.advice;

import com.seun.scheduler.domain.member.controller.MemberViewController;
import com.seun.scheduler.domain.member.dto.MemberProfileResponse;
import com.seun.scheduler.domain.member.service.MemberService;
import com.seun.scheduler.security.auth.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = {MemberViewController.class})
@RequiredArgsConstructor
public class ProfileDataAdvice {

    private final MemberService memberService;

    @ModelAttribute("member")
    public MemberProfileResponse getMyProfile(HttpServletRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {

        String uri = request.getRequestURI();

        if (uri.contains("/members/me") || "/members/edit".equals(uri)) {

            return memberService.getMyProfile(userDetails.getUsername());
        }

        return null;
    }
}