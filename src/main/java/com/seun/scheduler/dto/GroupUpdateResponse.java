package com.seun.scheduler.dto;

import com.seun.scheduler.domain.Group;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GroupUpdateResponse {
    private String name;
    private String groupImage;
    private List<UserResponse> members;

    public static GroupUpdateResponse from(Group group, List<UserResponse> members) {
        return GroupUpdateResponse.builder()
                .name(group.getName())
                .groupImage(group.getGroupImage())
                .members(members)
                .build();
    }
}