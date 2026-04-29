package com.seun.scheduler.domain.group.dto;

import com.seun.scheduler.domain.group.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupInvitationResponse {

    private Long id;
    private String name;
    private String description;
    private String groupImage;
    private Long memberCount;

    public static GroupInvitationResponse of(Group group, Long memberCount) {

        return GroupInvitationResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .groupImage(group.getGroupImage())
                .memberCount(memberCount)
                .build();
    }
}