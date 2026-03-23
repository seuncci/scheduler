package com.seun.scheduler.domain.member.controller;

import com.seun.scheduler.domain.group.dto.GroupMemberInfo;
import com.seun.scheduler.dto.GroupDetailResponse;
import com.seun.scheduler.security.auth.CustomUserDetails;
import com.seun.scheduler.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberViewController {

    private final GroupService groupService;

    @GetMapping("/login")
    public String loginPage() {
        return "/member/login";
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "/member/signup";
    }

    @GetMapping("/me")
    public String profilePage() {
        return "/member/me";
    }

    @GetMapping("/edit")
    public String editPage() {
        return "/member/edit";
    }

    @GetMapping("/me/groups")
    public String groupPage(@AuthenticationPrincipal CustomUserDetails userDetails, @PageableDefault(size = 9, sort = "createdDate") Pageable pageable, Model model) {

        model.addAttribute("groups", groupService.getMyGroupList(userDetails.getUsername(), pageable));

        return "/member/group-list";
    }

    @GetMapping("/me/groups/{groupId}")
    public String groupDetailPage(@PathVariable("groupId") Long groupId, @PageableDefault(size = 9, sort = "createdDate") Pageable pageable, Model model) {

        model.addAttribute("group", groupService.getGroup(groupId));
        model.addAttribute("members", groupService.getGroupMembers(groupId, pageable));

        return "/member/group-detail";
    }
}