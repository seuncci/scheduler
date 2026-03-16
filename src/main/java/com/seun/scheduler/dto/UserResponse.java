package com.seun.scheduler.dto;

import com.seun.scheduler.domain.GroupUser;
import com.seun.scheduler.domain.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private String userId;
    private String name;
    private String profileImage;
    private UserRole role;

    public static UserResponse of(GroupUser user) {
        return UserResponse.builder()
                .userId(user.getMember().getMemberId())
                .name(user.getMember().getName())
                .profileImage(user.getMember().getProfileImage())
                .role(user.getRole())
                .build();
    }
}
