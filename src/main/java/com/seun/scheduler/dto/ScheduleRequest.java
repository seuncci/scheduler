package com.seun.scheduler.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleRequest {
    @NotBlank(message = "일정명은 필수입니다.")
    private String title;

    private String content;

    private String location;

    @NotNull(message = "시작시간은 필수입니다.")
    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;
}
