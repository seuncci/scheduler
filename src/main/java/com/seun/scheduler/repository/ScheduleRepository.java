package com.seun.scheduler.repository;


import com.seun.scheduler.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository <Schedule, Long> {
    @Query("SELECT s FROM Schedule s JOIN FETCH s.user WHERE s.id = :scheduleId")
    Optional<Schedule> findByIdWithUser(@Param("scheduleId") Long scheduleId);
}
