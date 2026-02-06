package com.seun.scheduler.dto;

import com.seun.scheduler.domain.Schedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ScheduleResponse {
    private long id;
    private long groupId;
    private String userId;
    private String title;
    private String content;
    private String location;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public static ScheduleResponse from(Schedule schedule, String userId) {

        return ScheduleResponse.builder()
                .id(schedule.getId())
                .userId(userId)
                .title(schedule.getTitle())
                .content(schedule.getContent())
                .location(schedule.getLocation())
                .startDateTime(schedule.getStartDateTime())
                .endDateTime(schedule.getEndDateTime())
                .build();
    }

    public static ScheduleResponse from(Schedule schedule, String userId, long groupId) {

        return ScheduleResponse.builder()
                .id(schedule.getId())
                .groupId(groupId)
                .userId(userId)
                .title(schedule.getTitle())
                .content(schedule.getContent())
                .location(schedule.getLocation())
                .startDateTime(schedule.getStartDateTime())
                .endDateTime(schedule.getEndDateTime())
                .build();
    }
}
