package com.seun.scheduler.controller;

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
}
