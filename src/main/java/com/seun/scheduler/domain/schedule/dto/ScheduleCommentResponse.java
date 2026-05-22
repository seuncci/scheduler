package com.seun.scheduler.domain.schedule.dto;

import com.seun.scheduler.domain.schedule.entity.ScheduleComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleCommentResponse {

    private Long id;

    private String content;

    private String profileName;
    private String profileImage;

    private LocalDateTime createdDate;

    private Boolean isOwner;

    public static ScheduleCommentResponse of(ScheduleComment comment, String memberId) {

        return ScheduleCommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .profileName(comment.getMember().getName())
                .profileImage(comment.getMember().getProfileImage())
                .createdDate(comment.getCreatedDate())
                .isOwner(memberId.equals(comment.getMember().getMemberId()))
                .build();
    }
}