package com.seun.scheduler.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_invitation")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupInvitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id")
    private User inviter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id")
    private User invitee;

    @Enumerated(EnumType.STRING)
    private InvitationStatus status;

    @CreatedDate
    @Column(name = "request_time", updatable = false)
    private LocalDateTime requestTime;

    public void updateStatus(InvitationStatus status) {
        this.status = status;
    }
}
