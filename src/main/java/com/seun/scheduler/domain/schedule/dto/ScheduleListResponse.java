package com.seun.scheduler.domain.schedule.dto;

import com.seun.scheduler.domain.schedule.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleListResponse {

    private Long id;
    private String title;
    private String color;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public static ScheduleListResponse from(Schedule schedule) {

        return ScheduleListResponse.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .color(schedule.getColor())
                .startDateTime(schedule.getStartDateTime())
                .endDateTime(schedule.getEndDateTime())
                .build();
    }
}