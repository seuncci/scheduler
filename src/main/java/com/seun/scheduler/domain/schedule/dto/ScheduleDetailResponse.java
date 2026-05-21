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
public class ScheduleDetailResponse {

    private Long id;
    private Long groupId;

    private String groupName;
    private String groupImage;
    private String profileName;
    private String profileImage;

    private String title;
    private String content;
    private String location;
    private String color;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    private Boolean isCompleted;

    public static ScheduleDetailResponse from(Schedule schedule) {

        Long groupId = null;
        String groupName = null;
        String groupImage = null;
        String profileName = null;
        String profileImage = null;

        if (schedule.getGroup() != null) {

            groupId = schedule.getGroup().getId();
            groupName = schedule.getGroup().getName();
            groupImage = schedule.getGroup().getGroupImage();
            profileName = schedule.getMember().getName();
            profileImage = schedule.getMember().getProfileImage();
        }

        return ScheduleDetailResponse.builder()
                .id(schedule.getId())
                .groupId(groupId)
                .groupName(groupName)
                .groupImage(groupImage)
                .profileName(profileName)
                .profileImage(profileImage)
                .title(schedule.getTitle())
                .content(schedule.getContent())
                .location(schedule.getLocation())
                .color(schedule.getColor())
                .startDateTime(schedule.getStartDateTime())
                .endDateTime(schedule.getEndDateTime())
                .isCompleted(schedule.getIsCompleted())
                .build();
    }
}