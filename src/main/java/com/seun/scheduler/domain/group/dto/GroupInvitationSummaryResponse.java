package com.seun.scheduler.domain.group.dto;

import com.seun.scheduler.domain.group.config.GroupPolicy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupInvitationSummaryResponse {

    private int currentCount;

    @Builder.Default
    private int maxCount = GroupPolicy.MAX_INVITATION_LINK_COUNT;
    private List<GroupInvitationLinkResponse> links;

    public static GroupInvitationSummaryResponse form(List<GroupInvitationLinkResponse> links) {

        return GroupInvitationSummaryResponse.builder()
                .currentCount(links.size())
                .links(links)
                .build();
    }
}