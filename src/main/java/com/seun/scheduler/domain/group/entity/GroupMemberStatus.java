package com.seun.scheduler.domain.group.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum GroupMemberStatus {

    ACTIVE("활동"),
    RESIGNED("탈퇴"),
    KICKED("추방");

    private String name;
}