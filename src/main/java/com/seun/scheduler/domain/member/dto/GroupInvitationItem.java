package com.seun.scheduler.domain.member.dto;

import com.seun.scheduler.domain.group.entity.GroupInviteMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupInvitationItem {

    private Long id;
    private String name;
    private String groupImage;

    public static GroupInvitationItem from(GroupInviteMember inviteGroup) {

        return GroupInvitationItem.builder()
                .id(inviteGroup.getId())
                .name(inviteGroup.getGroup().getName())
                .groupImage(inviteGroup.getGroup().getGroupImage())
                .build();
    }
}