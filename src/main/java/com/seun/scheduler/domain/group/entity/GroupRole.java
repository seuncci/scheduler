package com.seun.scheduler.domain.group.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum GroupRole {

    LEADER("관리자"),
    MEMBER("멤버");

    private String name;
}