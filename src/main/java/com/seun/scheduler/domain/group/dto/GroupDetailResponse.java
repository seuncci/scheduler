package com.seun.scheduler.domain.group.dto;

import com.seun.scheduler.domain.group.entity.Group;
import com.seun.scheduler.domain.group.entity.GroupRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupDetailResponse {

    private Long id;
    private String name;
    private String description;
    private String groupImage;
    private LocalDateTime createdDate;
    private GroupRole role;
    private Long memberCount;

    public static GroupDetailResponse of(Group group, GroupRole role, Long memberCount) {

        return GroupDetailResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .groupImage(group.getGroupImage())
                .createdDate(group.getCreatedDate())
                .role(role)
                .memberCount(memberCount)
                .build();
    }
}