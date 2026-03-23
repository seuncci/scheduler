package com.seun.scheduler.domain.member.entity;

import com.seun.scheduler.domain.group.entity.GroupMember;
import com.seun.scheduler.domain.member.dto.MemberJoinRequest;
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
@Table(name = "members")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false, length = 10)
    private String memberId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    private String profileImage;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    List<GroupMember> groups = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdTime;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    public void updatePassword(String password) {

        this.password = password;
    }

    public void updateProfileImage(String profileImage) {

        this.profileImage = profileImage;
    }

    public void updateProfile(String name, String email) {

        this.name = name;
        this.email = email;
    }

    public static Member of(MemberJoinRequest request, String password) {

        return Member.builder()
                .memberId(request.getMemberId())
                .password(password)
                .name(request.getName())
                .email(request.getEmail())
                .build();
    }
}