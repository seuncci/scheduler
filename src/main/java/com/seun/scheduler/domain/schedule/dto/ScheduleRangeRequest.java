package com.seun.scheduler.domain.schedule.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRangeRequest {

    private LocalDate startDate;
    private LocalDate endDate;
}