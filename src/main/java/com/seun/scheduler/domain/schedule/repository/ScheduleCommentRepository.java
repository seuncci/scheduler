package com.seun.scheduler.domain.schedule.repository;

import com.seun.scheduler.domain.schedule.entity.ScheduleComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ScheduleCommentRepository extends JpaRepository<ScheduleComment, Long> {

    @EntityGraph(attributePaths = {"member"})
    Page<ScheduleComment> findByScheduleIdAndDeletedAtIsNull(Long scheduleId, Pageable pageable);

    Optional<ScheduleComment> findByIdAndScheduleIdAndMemberMemberId(Long id, Long scheduleId, String memberId);
}