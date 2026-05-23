package com.seun.scheduler.domain.schedule.entity;

import com.seun.scheduler.domain.group.entity.Group;
import com.seun.scheduler.domain.member.entity.Member;
import com.seun.scheduler.domain.schedule.dto.ScheduleCreateRequest;
import com.seun.scheduler.domain.schedule.dto.ScheduleUpdateRequest;
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
@Table(name = "schedules", indexes = {
        @Index(name = "idx_schedule_member_date", columnList = "member_id, startDateTime, endDateTime"),
        @Index(name = "idx_schedule_group_date", columnList = "group_id, startDateTime, endDateTime")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String content;

    private String location;

    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    private String color;

    private Boolean isCompleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleComment> comments = new ArrayList<>();

    private LocalDateTime deletedDate;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    public void delete() {
        this.deletedDate = LocalDateTime.now();
    }

    public Boolean isDeleted() {
        return this.deletedDate != null;
    }

    public void update(ScheduleUpdateRequest request, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        this.title = request.getTitle();
        this.content = request.getContent();
        this.location = request.getLocation();
        this.color = request.getColor();
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.isCompleted = request.getIsCompleted();
    }

    public static Schedule of(LocalDateTime startDateTime, LocalDateTime endDateTime, Member member, Group group, ScheduleCreateRequest request) {

        return Schedule.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .location(request.getLocation())
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .color(request.getColor())
                .isCompleted(false)
                .member(member)
                .group(group)
                .build();
    }
}