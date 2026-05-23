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

    @DeleteMapping("/{scheduleId}")
    public CommonResponse<Void> deleteSchedule(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("scheduleId") Long scheduleId) {

        scheduleService.deleteSchedule(userDetails.getUsername(), scheduleId);
        return CommonResponse.result(ResultCode.SCHEDULE_DELETE_SUCCESS);
    }
}