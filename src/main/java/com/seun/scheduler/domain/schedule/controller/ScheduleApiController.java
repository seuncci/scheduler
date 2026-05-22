package com.seun.scheduler.domain.schedule.controller;

import com.seun.scheduler.domain.schedule.dto.*;
import com.seun.scheduler.global.common.CommonResponse;
import com.seun.scheduler.global.common.ResultCode;
import com.seun.scheduler.security.auth.CustomUserDetails;
import com.seun.scheduler.domain.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleApiController {

    private final ScheduleService scheduleService;

    @PostMapping
    public CommonResponse<Void> createSchedule(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody ScheduleCreateRequest request) {

        scheduleService.createSchedule(userDetails.getUsername(), request);

        return CommonResponse.result(ResultCode.SCHEDULE_CREATE_SUCCESS);
    }

    @GetMapping
    public CommonResponse<List<ScheduleListResponse>> getSchedulesByRange(@AuthenticationPrincipal CustomUserDetails userDetails, @ModelAttribute ScheduleRangeRequest request) {

        return CommonResponse.result(ResultCode.SCHEDULE_GET_SUCCESS, scheduleService.getSchedulesByRange(userDetails.getUsername(), request));
    }

    @GetMapping("/{scheduleId}")
    public CommonResponse<ScheduleDetailResponse> getScheduleDetail(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("scheduleId") Long scheduleId) {

        return CommonResponse.result(ResultCode.SCHEDULE_DETAIL_SUCCESS, scheduleService.getScheduleDetail(userDetails.getUsername(), scheduleId));
    }

    @PutMapping("/{scheduleId}")
    public CommonResponse<Void> updateSchedule(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("scheduleId") Long scheduleId,
                                               @RequestBody @Valid ScheduleUpdateRequest request) {

        scheduleService.updateSchedule(userDetails.getUsername(), scheduleId, request);
        return CommonResponse.result(ResultCode.SCHEDULE_UPDATE_SUCCESS);
    }

    @PostMapping("/{scheduleId}/comments")
    public CommonResponse<Void> createComment(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("scheduleId") Long scheduleId,
                                              @RequestBody @Valid ScheduleCommentCreateRequest request) {

        scheduleService.createComment(userDetails.getUsername(), scheduleId, request);
        return CommonResponse.result(ResultCode.COMMENT_CREATE_SUCCESS);
    }

    @GetMapping("/{scheduleId}/comments")
    public CommonResponse<ScheduleCommentPageResponse> getCommentPage(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("scheduleId") Long scheduleId,
                                                                      @PageableDefault(size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return CommonResponse.result(ResultCode.COMMENT_GET_SUCCESS, scheduleService.getCommentPage(userDetails.getUsername(), scheduleId, pageable));
    }

    @PutMapping("/{scheduleId}/comments/{commentId}")
    public CommonResponse<Void> updateComment(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("scheduleId") Long scheduleId,
                                              @PathVariable("commentId") Long commentId, @RequestBody @Valid ScheduleCommentUpdateRequest request) {

        scheduleService.updateComment(userDetails.getUsername(), scheduleId, commentId, request);
        return CommonResponse.result(ResultCode.COMMENT_UPDATE_SUCCESS);
    }

    @DeleteMapping("/{scheduleId}/comments/{commentId}")
    public CommonResponse<Void> deleteComment(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("scheduleId") Long scheduleId,
                                              @PathVariable("commentId") Long commentId) {

        scheduleService.deleteComment(userDetails.getUsername(), scheduleId, commentId);
        return CommonResponse.result(ResultCode.COMMENT_DELETE_SUCCESS);
    }

    /*


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

    */
}