package com.seun.scheduler.domain.schedule.repository;

import com.seun.scheduler.domain.schedule.entity.ScheduleComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ScheduleCommentRepository extends JpaRepository<ScheduleComment, Long> {

    @EntityGraph(attributePaths = {"member"})
    Page<ScheduleComment> findByScheduleIdAndDeletedDateIsNull(Long scheduleId, Pageable pageable);

    Optional<ScheduleComment> findByIdAndScheduleIdAndMemberMemberId(Long id, Long scheduleId, String memberId);

    @Modifying
    @Query("UPDATE ScheduleComment sc SET sc.deletedDate = :now WHERE sc.schedule.id = :scheduleId AND sc.deletedDate IS NULL")
    void softDeleteAllByScheduleId(@Param("scheduleId") Long scheduleId, @Param("now") LocalDateTime now);
}