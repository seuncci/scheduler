package com.seun.scheduler.domain.schedule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ScheduleViewController {

    @GetMapping
    public String getMainSchedules() {

        return "schedule/main-schedules";
    }
}
