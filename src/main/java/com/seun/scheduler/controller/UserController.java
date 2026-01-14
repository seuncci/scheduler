package com.seun.scheduler.controller;

import com.seun.scheduler.dto.UserJoinRequest;
import com.seun.scheduler.service.UserSevice;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserSevice userSevice;

    @GetMapping("/login")
    public String loginTest() {
        return "success";
    }

    @PostMapping("/join")
    public ResponseEntity<String> join(@Valid @RequestBody UserJoinRequest dto) {
        userSevice.join(dto);

        return ResponseEntity.ok("success");
    }
}
