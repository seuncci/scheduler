package com.seun.scheduler.domain.group.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyGroupResponse {

    private Long id;
    private String name;
    private String description;
    private String groupImage;
    private Long memberCount;
    private String activityText;

    public MyGroupResponse(Long id, String name, String description, String groupImage, long memberCount, LocalDateTime latestModifiedDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.groupImage = groupImage;
        this.memberCount = memberCount;
        this.activityText = convertToActivityText(latestModifiedDate);
    }

    private String convertToActivityText(LocalDateTime targetDate) {

        if (targetDate == null) {

            return "활동 없음";
        }

        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(targetDate, now);

        if (minutes < 1) return "방금 전";
        if (minutes < 60) return minutes + "분 전";

        long hours = ChronoUnit.HOURS.between(targetDate, now);

        if (hours < 24) return hours + "시간 전";

        long days = ChronoUnit.DAYS.between(targetDate, now);

        if (days < 30) return days + "일 전";

        return targetDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }
}