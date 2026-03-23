package com.seun.scheduler.domain.group.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyGroupResponse {

    private Long id;
    private String name;
    private String description;
    private String groupImage;
    private Long memberCount;
}