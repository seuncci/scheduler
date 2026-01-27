package com.seun.scheduler.controller;

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
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest dto) {
        String token = userService.login(dto);

        return ResponseEntity.ok(Map.of("Token", token));
    }

    @PostMapping("/join")
    public ResponseEntity<String> join(@Valid @RequestBody UserJoinRequest dto) {
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
