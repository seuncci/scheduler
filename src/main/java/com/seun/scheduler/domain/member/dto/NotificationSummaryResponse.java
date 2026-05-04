package com.seun.scheduler.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSummaryResponse {

    private List<GroupInvitationItem> invitations;
    private Long count;
}