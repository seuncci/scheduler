package com.seun.scheduler.domain.group.dto;

import com.seun.scheduler.domain.group.entity.GroupRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupMemberInfo {

    private String memberId;
    private String name;
    private String email;
    private String profileImage;
    private GroupRole role;
}