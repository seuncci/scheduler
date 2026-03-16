package com.seun.scheduler.domain.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/members")
public class MemberViewController {
    @GetMapping("/login")
    public String loginPage() {
        return "/member/login";
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "/member/signup";
    }
}