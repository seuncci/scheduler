package com.seun.scheduler.domain.schedule.repository;


import com.seun.scheduler.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository <Schedule, Long> {

    @Query("SELECT s FROM Schedule s WHERE s.group.id IS NULL AND s.member.memberId = :memberId " +
    "AND ((s.startDateTime IS NOT NULL AND s.startDateTime <= :endDateTime AND s.endDateTime >= :startDateTime) " +
    " OR (s.startDateTime IS NULL AND s.endDateTime >= :startDateTime AND s.endDateTime <= :endDateTime))")
    List<Schedule> findPrivateSchedules(@Param("memberId") String memberId, @Param("startDateTime") LocalDateTime startDateTime,  @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT s FROM Schedule s JOIN GroupMember gm ON s.group.id = gm.group.id " +
            "WHERE gm.member.memberId = :memberId AND gm.status = 'ACTIVE' " +
            "AND ((s.startDateTime IS NOT NULL AND s.startDateTime <= :endDateTime AND s.endDateTime >= :startDateTime) " +
            " OR (s.startDateTime IS NULL AND s.endDateTime >= :startDateTime AND s.endDateTime <= :endDateTime))")
    List<Schedule> findGroupSchedules(@Param("memberId") String memberId, @Param("startDateTime") LocalDateTime startDateTime,  @Param("endDateTime") LocalDateTime endDateTime);
}