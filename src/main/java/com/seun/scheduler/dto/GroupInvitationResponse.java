package com.seun.scheduler.dto;

import com.seun.scheduler.domain.GroupInvitation;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GroupInvitationResponse {
    private long invitationId;
    private String groupName;
    private String inviterName;
    private LocalDateTime requestTime;

    public static GroupInvitationResponse of(GroupInvitation invitation) {

        return GroupInvitationResponse.builder()
                .invitationId(invitation.getId())
                .groupName(invitation.getGroup().getName())
                .inviterName(invitation.getInviter().getMemberId())
                .requestTime(invitation.getRequestTime())
                .build();
    }
}
