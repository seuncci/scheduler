package com.seun.scheduler.domain.group.controller;

import com.seun.scheduler.security.auth.CustomUserDetails;
import com.seun.scheduler.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupViewController {

    private final GroupService groupService;

    @GetMapping("/join")
    public String joinPage(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam String code, Model model) {

        model.addAttribute("group", groupService.getGroupForInvitation(code, userDetails.getUsername()));
        model.addAttribute("code", code);

        return "/group/join";
    }
}