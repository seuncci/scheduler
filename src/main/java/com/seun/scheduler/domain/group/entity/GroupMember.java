package com.seun.scheduler.domain.group.entity;

import com.seun.scheduler.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_member")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private GroupRole role;

    @Enumerated(EnumType.STRING)
    private GroupMemberStatus status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime lastJoinedDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    public void delegate(GroupRole role) {

        this.role = role;
    }

    public void rejoin() {

        this.status = GroupMemberStatus.ACTIVE;
        this.lastJoinedDate = LocalDateTime.now();
    }

    public void kick() {

        this.status = GroupMemberStatus.KICKED;
    }

    public void leave() {

        this.status = GroupMemberStatus.RESIGNED;
    }

    public static GroupMember of(Group group, Member member, GroupRole role) {

        return GroupMember.builder()
                .group(group)
                .member(member)
                .role(role)
                .status(GroupMemberStatus.ACTIVE)
                .build();
    }
}