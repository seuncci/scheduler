package com.seun.scheduler.domain.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleCommentCreateRequest {

    @NotBlank(message = "COMMENT_CONTENT_REQUIRED")
    private String content;
}