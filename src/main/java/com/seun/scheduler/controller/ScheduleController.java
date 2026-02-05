package com.seun.scheduler.controller;

import com.seun.scheduler.dto.CommonResponse;
import com.seun.scheduler.dto.ScheduleRequest;
import com.seun.scheduler.dto.ScheduleResponse;
import com.seun.scheduler.security.UserDetailsImpl;
import com.seun.scheduler.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
