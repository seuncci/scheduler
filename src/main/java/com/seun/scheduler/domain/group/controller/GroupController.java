package com.seun.scheduler.domain.group.controller;

import com.seun.scheduler.domain.group.dto.GroupCreateRequest;
import com.seun.scheduler.domain.group.dto.GroupInviteRequest;
import com.seun.scheduler.domain.group.dto.GroupUpdateRequest;
import com.seun.scheduler.global.common.CommonResponse;
import com.seun.scheduler.global.common.ResultCode;
import com.seun.scheduler.security.auth.CustomUserDetails;
import com.seun.scheduler.domain.group.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public CommonResponse<Void> createGroup(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @Valid @RequestPart(value = "data") GroupCreateRequest request,
                                            @RequestPart(value = "image", required = false) MultipartFile groupImage) {

        groupService.createGroup(userDetails.getUsername(), request, groupImage);
        return CommonResponse.result(ResultCode.GROUP_CREATE_SUCCESS);
    }

    @PatchMapping("/{groupId}")
    public CommonResponse<Void> updateGroup(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @Valid @RequestPart(value = "data") GroupUpdateRequest request,
                                            @RequestPart(value = "image", required = false) MultipartFile groupImage,
                                            @PathVariable("groupId") Long groupId) {

        groupService.updateGroup(userDetails.getUsername(), request, groupImage, groupId);
        return CommonResponse.result(ResultCode.GROUP_UPDATE_SUCCESS);
    }

    @PostMapping("/{groupId}/invitation-link")
    public CommonResponse<Void> createGroupInvitationLink(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                          @RequestParam Long expiredAt,
                                                          @PathVariable("groupId") Long groupId) {

        groupService.createGroupInvitationLink(userDetails.getUsername(), expiredAt, groupId);
        return CommonResponse.result(ResultCode.INVITE_LINK_CREATE_SUCCESS);
    }

    @DeleteMapping("/{groupId}/invitation-link/{linkId}")
    public CommonResponse<Void> deleteInvitationLink(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                     @PathVariable("groupId") Long groupId,
                                                     @PathVariable("linkId") Long linkId) {

        groupService.deleteInvitationLink(userDetails.getUsername(), groupId, linkId);
        return CommonResponse.result(ResultCode.INVITE_LINK_DELETE_SUCCESS);
    }

    @PostMapping("/join")
    public CommonResponse<Void> joinGroup(@RequestParam String code, @AuthenticationPrincipal CustomUserDetails userDetails) {

        groupService.joinGroup(code, userDetails.getUsername());
        return CommonResponse.result(ResultCode.GROUP_JOIN_SUCCESS);
    }

    @PostMapping("/{groupId}/members/{memberId}/kick")
    public CommonResponse<Void> kickMember(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @PathVariable("groupId") Long groupId,
                                           @PathVariable("memberId") String memberId) {

        groupService.kickMember(userDetails.getUsername(), memberId, groupId);
        return CommonResponse.result(ResultCode.GROUP_MEMBER_KICK_SUCCESS);
    }

    @PostMapping("/{groupId}/members/leave")
    public CommonResponse<Void> leaveGroup(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("groupId") Long groupId) {

        groupService.leaveGroup(userDetails.getUsername(), groupId);
        return CommonResponse.result(ResultCode.GROUP_LEAVE_SUCCESS);
    }

    @PostMapping("/{groupId}/members/{memberId}/transfer")
    public CommonResponse<Void> transferOwnership(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @PathVariable("groupId") Long groupId,
                                                  @PathVariable("memberId") String memberId) {

        groupService.transferOwnership(userDetails.getUsername(), memberId, groupId);
        return CommonResponse.result(ResultCode.GROUP_DELEGATE_SUCCESS);
    }

    @DeleteMapping("/{groupId}")
    public CommonResponse<Void> deleteGroup(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("groupId") Long groupId) {

        groupService.deleteGroup(userDetails.getUsername(), groupId);
        return CommonResponse.result(ResultCode.GROUP_DELETE_SUCCESS);
    }

    @PostMapping("/{groupId}/invitations")
    public CommonResponse<Void> inviteMember(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable("groupId") Long groupId,
                                             @RequestBody GroupInviteRequest request) {

        groupService.inviteMember(userDetails.getUsername(), request.getMemberId(), groupId);
        return CommonResponse.result(ResultCode.GROUP_INVITE_SUCCESS);
    }
}