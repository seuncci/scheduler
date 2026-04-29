package com.seun.scheduler.domain.group.dto;

import com.seun.scheduler.domain.group.entity.GroupInvitationLink;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupInvitationLinkResponse {

    private Long id;
    private String code;
    private LocalDateTime expireDate;

    public static GroupInvitationLinkResponse from(GroupInvitationLink link) {

        return GroupInvitationLinkResponse.builder()
                .id(link.getId())
                .code(link.getCode())
                .expireDate(link.getExpireDate())
                .build();
    }
}