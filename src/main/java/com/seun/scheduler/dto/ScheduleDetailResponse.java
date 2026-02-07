package com.seun.scheduler.dto;

import com.seun.scheduler.domain.Schedule;
import com.seun.scheduler.domain.ScheduleComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class ScheduleDetailResponse {
    private long id;
    private String userId;
    private String groupName;
    private String title;
    private String content;
    private String location;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocalDateTime createTime;
    private List<ScheduleCommentResponse> comments;

    public static ScheduleDetailResponse of(Schedule schedule) {
        List<ScheduleCommentResponse> comments = new ArrayList<>();
        String groupName = null;

        // 그룹 일정일 경우 그룹 이름 가져오기
        if (schedule.getGroup() != null) {
            groupName = schedule.getGroup().getName();
        }

        // 댓글이 있을 경우 댓글 목록 가져오기
        for (ScheduleComment comment : schedule.getComments()) {
            comments.add(ScheduleCommentResponse.from(comment, comment.getSchedule().getId(), comment.getUser().getUserId()));
        }

        return ScheduleDetailResponse.builder()
                .id(schedule.getId())
                .userId(schedule.getUser().getUserId())
                .groupName(groupName)
                .title(schedule.getTitle())
                .content(schedule.getContent())
                .location(schedule.getLocation())
                .startDateTime(schedule.getStartDateTime())
                .endDateTime(schedule.getEndDateTime())
                .createTime(schedule.getCreateTime())
                .comments(comments)
                .build();

    }
}
