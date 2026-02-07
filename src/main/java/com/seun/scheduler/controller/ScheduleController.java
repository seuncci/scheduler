package com.seun.scheduler.controller;

import com.seun.scheduler.dto.*;
import com.seun.scheduler.security.UserDetailsImpl;
import com.seun.scheduler.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<CommonResponse<ScheduleResponse>> createPersonalSchedule(
                @AuthenticationPrincipal UserDetailsImpl userDetails,
                @Valid @RequestBody ScheduleRequest request
            ) {

        ScheduleResponse schedule = scheduleService.createPersonalSchedule(userDetails.getUsername(), request);

        CommonResponse<ScheduleResponse> response = CommonResponse.<ScheduleResponse> builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK.name())
                .message("등록 성공")
                .data(schedule)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/groups/{groupId}")
    public ResponseEntity<CommonResponse<ScheduleResponse>> createGroupSchedule(
            @PathVariable("groupId") long groupId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ScheduleRequest request
    ) {
        ScheduleResponse schedule = scheduleService.createGroupSchedule(groupId, userDetails.getUsername(), request);

        CommonResponse<ScheduleResponse> response = CommonResponse.<ScheduleResponse> builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK.name())
                .message("등록 성공")
                .data(schedule)
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{scheduleId}")
    public ResponseEntity<CommonResponse<ScheduleResponse>> updateSchedule(
            @PathVariable("scheduleId") long scheduleId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ScheduleRequest request
    ) {
        ScheduleResponse schedule = scheduleService.updateSchedule(scheduleId, userDetails.getUsername(), request);

        CommonResponse<ScheduleResponse> response = CommonResponse.<ScheduleResponse> builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK.name())
                .message("수정 성공")
                .data(schedule)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<CommonResponse<Void>> deleteSchedule(
            @PathVariable("scheduleId") long scheduleId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        scheduleService.deleteSchedule(scheduleId, userDetails.getUsername());

        CommonResponse<Void> response = CommonResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK.name())
                .message("삭제 성공")
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{scheduleId}/comments")
    public ResponseEntity<CommonResponse<ScheduleCommentResponse>> createComment(
            @PathVariable("scheduleId") long scheduleId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ScheduleCommentRequest request
            ) {
        ScheduleCommentResponse commentResponse = scheduleService.createComment(scheduleId, userDetails.getUsername(), request);

        CommonResponse<ScheduleCommentResponse> response = CommonResponse.<ScheduleCommentResponse>builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK.name())
                .message("댓글 등록 성공")
                .data(commentResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommonResponse<ScheduleCommentResponse>> updateComment(
            @PathVariable("commentId") long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ScheduleCommentRequest request
    ) {
        ScheduleCommentResponse commentResponse = scheduleService.updateComment(commentId, userDetails.getUsername(), request);

        CommonResponse<ScheduleCommentResponse> response = CommonResponse.<ScheduleCommentResponse>builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK.name())
                .message("댓글 수정 성공")
                .data(commentResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<CommonResponse<Void>> deleteComment(
            @PathVariable("commentId") long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        scheduleService.deleteComment(commentId, userDetails.getUsername());

        CommonResponse<Void> response = CommonResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK.name())
                .message("삭제 성공")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<CommonResponse<ScheduleDetailResponse>> getScheduleDetail(
            @PathVariable("scheduleId") long scheduleId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        ScheduleDetailResponse detailResponse = scheduleService.getScheduleDetail(scheduleId, userDetails.getUsername());

        CommonResponse<ScheduleDetailResponse> response = CommonResponse.<ScheduleDetailResponse>builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK.name())
                .message("조회 성공")
                .data(detailResponse)
                .build();

        return ResponseEntity.ok(response);
    }
}
