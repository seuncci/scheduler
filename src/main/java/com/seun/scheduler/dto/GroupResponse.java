package com.seun.scheduler.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupResponse {
    private long groupId;
    private String name;
    private String groupImage;
}