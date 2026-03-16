package com.seun.scheduler.repository;

import com.seun.scheduler.domain.ScheduleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ScheduleCommentRepository extends JpaRepository<ScheduleComment, Long> {
    @Query("SELECT c FROM ScheduleComment c JOIN FETCH c.member WHERE c.id = :commentId")
    Optional<ScheduleComment> findByIdWithMember(@Param("commentId") Long commentId);
}
