package com.seun.scheduler.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ScheduleCommentRequest {
    @NotBlank(message = "내용은 필수입니다.")
    private String content;
}
