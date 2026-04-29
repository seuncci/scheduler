package com.seun.scheduler.domain.group.entity;

import com.seun.scheduler.domain.group.dto.GroupCreateRequest;
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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "groups")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String groupImage;

    @Builder.Default
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> members = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    public void updateGroupInfo(String name, String description) {

        this.name = name;
        this.description = description;
    }

    public void updateGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public void updateMember(Member member) {

        this.members.add(GroupMember.of(this, member, GroupRole.LEADER));
    }

    public static Group from(GroupCreateRequest request) {

        return Group.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }
}