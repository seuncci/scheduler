package com.seun.scheduler.domain.schedule.dto;

import com.seun.scheduler.domain.schedule.entity.ScheduleComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleCommentPageResponse {

    List<ScheduleCommentResponse> comments;

    int currentPage;
    Long totalCommentCount;

    public static ScheduleCommentPageResponse of(Page<ScheduleComment> commentPage, String memberId) {

        return ScheduleCommentPageResponse.builder()
                .comments(commentPage.getContent().stream().map(comment -> ScheduleCommentResponse.of(comment, memberId)).toList())
                .currentPage(commentPage.getNumber())
                .totalCommentCount(commentPage.getTotalElements())
                .build();
    }
}