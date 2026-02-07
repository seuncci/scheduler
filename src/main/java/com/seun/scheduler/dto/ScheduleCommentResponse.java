package com.seun.scheduler.dto;

import com.seun.scheduler.domain.ScheduleComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ScheduleCommentResponse {
    private long id;
    private long scheduleId;
    private String userId;
    private String content;
    private LocalDateTime createTime;

    public static ScheduleCommentResponse from(ScheduleComment comment, long scheduleId, String userId) {
        return ScheduleCommentResponse.builder()
                .id(comment.getId())
                .scheduleId(scheduleId)
                .userId(userId)
                .content(comment.getContent())
                .createTime(comment.getCreateTime())
                .build();

    }
}
