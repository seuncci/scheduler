package com.seun.scheduler.controller;

import com.seun.scheduler.dto.CommonResponse;
import com.seun.scheduler.dto.GroupCreateRequest;
import com.seun.scheduler.dto.GroupResponse;
import com.seun.scheduler.security.UserDetailsImpl;
import com.seun.scheduler.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
}
