package com.seun.scheduler.repository;


import com.seun.scheduler.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository <Schedule, Long> {
}
