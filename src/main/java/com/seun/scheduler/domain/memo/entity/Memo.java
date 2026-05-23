package com.seun.scheduler.domain.memo.entity;

import com.seun.scheduler.domain.member.entity.Member;
import com.seun.scheduler.domain.memo.dto.MemoCreateRequest;
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
@Table(name = "memos")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime deletedDate;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    public void delete() {
        this.deletedDate = LocalDateTime.now();
    }
    /*
    public boolean isDeleted() {
        return this.deletedDate != null;
    }
    */
    public void update(String content, String category) {

        this.content = content;
        this.category = category;
    }

    public static Memo of(Member member, MemoCreateRequest request) {

        return Memo.builder()
                .content(request.getContent())
                .category(request.getCategory())
                .member(member)
                .build();
    }
}