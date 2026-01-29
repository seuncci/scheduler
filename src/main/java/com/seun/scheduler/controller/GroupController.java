package com.seun.scheduler.controller;

import com.seun.scheduler.domain.InvitationStatus;
import com.seun.scheduler.dto.*;
import com.seun.scheduler.security.UserDetailsImpl;
import com.seun.scheduler.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<CommonResponse<Void>> createGroup(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestPart(value = "data") GroupCreateRequest request,
            @RequestPart(value = "image") MultipartFile image
            ) throws IOException {

        return groupService.create(userDetails.getUsername(), request, image);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<GroupResponse>>> getMyGroupList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return groupService.getMyGroupList(userDetails.getUsername());
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<CommonResponse<GroupDetailResponse>> getGroupDetail(@PathVariable("groupId") long groupId) {

        CommonResponse<GroupDetailResponse> response = CommonResponse.<GroupDetailResponse> builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK.name())
                .message("조회 성공")
                .data(groupService.getGroupDetail(groupId))
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{groupId}")
    public ResponseEntity<CommonResponse<GroupUpdateResponse>> updateGroup(
            @PathVariable("groupId") long groupId, @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestPart(value = "data") GroupUpdateRequest request, @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        CommonResponse<GroupUpdateResponse> response = CommonResponse.<GroupUpdateResponse> builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK.name())
                .message("수정 성공")
                .data(groupService.updateGroup(userDetails.getUsername(), groupId, request, image))
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<CommonResponse<Void>> leaveGroup (@PathVariable("groupId") Long groupId, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        groupService.leaveGroup(groupId, userDetails.getUsername());

        CommonResponse<Void> response = CommonResponse.<Void> builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK.name())
                .message("탈퇴 완료")
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<CommonResponse<Void>> deleteGroup (@PathVariable("groupId") Long groupId, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        groupService.deleteGroup(groupId, userDetails.getUsername());

        CommonResponse<Void> response = CommonResponse.<Void> builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK.name())
                .message("삭제 완료")
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{groupId}/leader")
    public ResponseEntity<CommonResponse<Void>> delegateLeader (@PathVariable("groupId") Long groupId, @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                @RequestParam("targetMemberId") String targetMemberId) {
        groupService.delegateLeader(groupId, userDetails.getUsername(), targetMemberId);

        CommonResponse<Void> response = CommonResponse.<Void> builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK.name())
                .message("그룹장 위임 완료")
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{groupId}/invitation")
    public ResponseEntity<CommonResponse<Void>> inviteMember (
            @PathVariable("groupId") Long groupId, @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("targetMemberId") String targetMemberId
    ) {

        groupService.inviteMember(groupId, userDetails.getUsername(), targetMemberId);

        CommonResponse<Void> response = CommonResponse.<Void> builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK.name())
                .message("그룹원 초대 요청 완료")
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/invitations/{invitationId}")
    public ResponseEntity<CommonResponse<Void>> processInvitation (
            @PathVariable("invitationId") Long invitationId, @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("status") InvitationStatus status
            ) {

        String message = "초대";

        groupService.processInvitation(invitationId, userDetails.getUsername(), status);

        if (status == InvitationStatus.ACCEPTED) message += " 수락";
        else if (status == InvitationStatus.REJECTED) message += " 거절";

        CommonResponse<Void> response = CommonResponse.<Void> builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK.name())
                .message(message)
                .build();

        return ResponseEntity.ok(response);
    }
}
