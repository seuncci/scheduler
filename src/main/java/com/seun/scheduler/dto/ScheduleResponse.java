package com.seun.scheduler.dto;

import com.seun.scheduler.domain.Schedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ScheduleResponse {
    private long id;
    private String userId;
    private String title;
    private String content;
    private String location;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public static ScheduleResponse of(Schedule schedule) {

        return ScheduleResponse.builder()
                .id(schedule.getId())
                .userId(schedule.getUser().getUserId())
                .title(schedule.getTitle())
                .content(schedule.getContent())
                .location(schedule.getLocation())
                .startDateTime(schedule.getStartDateTime())
                .endDateTime(schedule.getEndDateTime())
                .build();
    }
}
