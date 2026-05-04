package com.seun.scheduler.domain.member.controller;

import com.seun.scheduler.security.auth.CustomUserDetails;
import com.seun.scheduler.domain.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public String groupPage(@AuthenticationPrincipal CustomUserDetails userDetails, @PageableDefault(size = 6, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable, Model model) {

        model.addAttribute("groups", groupService.getMyGroupList(userDetails.getUsername(), pageable));

        return "/member/group-list";
    }

    @GetMapping("/me/groups/{groupId}")
    public String groupDetailPage(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("groupId") Long groupId, @PageableDefault Pageable pageable, Model model) {

        model.addAttribute("group", groupService.getGroup(groupId, userDetails.getUsername()));
        model.addAttribute("members", groupService.getGroupMembers(groupId, pageable));

        return "/member/group-detail";
    }

    @GetMapping("/me/groups/{groupId}/invitation-links")
    public String groupInvitationPage(@AuthenticationPrincipal CustomUserDetails userDetails,
                                      @PathVariable("groupId") Long groupId, Model model) {

        model.addAttribute("group", groupService.getGroup(groupId, userDetails.getUsername()));
        model.addAttribute("links", groupService.getInvitationLinks(userDetails.getUsername(), groupId));

        return "/member/group-invitation-links";
    }
}