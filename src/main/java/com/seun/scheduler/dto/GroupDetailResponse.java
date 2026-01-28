package com.seun.scheduler.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GroupDetailResponse {
    private String name;
    private String groupImage;
    private List<UserResponse> users;
}
