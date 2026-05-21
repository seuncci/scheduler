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
    private Long groupId;
    private String title;
    private String location;
    private String color;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public static ScheduleListResponse from(Schedule schedule) {

        Long groupId = (schedule.getGroup() != null) ? schedule.getGroup().getId() : null;

        return ScheduleListResponse.builder()
                .id(schedule.getId())
                .groupId(groupId)
                .title(schedule.getTitle())
                .location(schedule.getLocation())
                .color(schedule.getColor())
                .startDateTime(schedule.getStartDateTime())
                .endDateTime(schedule.getEndDateTime())
                .build();
    }
}