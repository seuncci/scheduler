package com.seun.scheduler.repository;


import com.seun.scheduler.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository <Schedule, Long> {
    @Query("SELECT s FROM Schedule s JOIN FETCH s.member WHERE s.id = :scheduleId")
    Optional<Schedule> findByIdWithUser(@Param("scheduleId") Long scheduleId);
    @Query("SELECT s FROM Schedule s JOIN FETCH s.member LEFT JOIN FETCH s.group LEFT JOIN FETCH s.comments c LEFT JOIN FETCH c.member WHERE s.id = :scheduleId")
    Optional<Schedule> findByIdWithAll(@Param("scheduleId") Long scheduleId);
}
