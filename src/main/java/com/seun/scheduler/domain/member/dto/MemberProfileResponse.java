package com.seun.scheduler.domain.member.dto;

import com.seun.scheduler.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberProfileResponse {

    private String name;
    private String email;
    private String profileImage;

    public static MemberProfileResponse from(Member member) {

        return MemberProfileResponse.builder()
                .name(member.getName())
                .email(member.getEmail())
                .profileImage(member.getProfileImage())
                .build();
    }
}
