package com.seun.scheduler.domain.member.controller;

import com.seun.scheduler.domain.member.dto.MemberJoinRequest;
import com.seun.scheduler.dto.*;
import com.seun.scheduler.security.UserDetailsImpl;
import com.seun.scheduler.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberApiController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> join(@Valid @RequestBody MemberJoinRequest dto) {
        userService.join(dto);

        return ResponseEntity.ok("success");
    }

    @PostMapping("/profile")
    public ResponseEntity<CommonResponse<UserProfileResponse>> getUserProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getUser(userDetails.getUsername());
    }

    @PatchMapping("/profile")
    public ResponseEntity<CommonResponse<UserProfileResponse>> updateUserProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestPart(value = "data") UpdateUserRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
            ) throws IOException {
        return userService.updateUser(userDetails.getUsername(), request, image);
    }
}
