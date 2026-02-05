package com.seun.scheduler.service;

import com.seun.scheduler.domain.Schedule;
import com.seun.scheduler.domain.User;
import com.seun.scheduler.dto.ScheduleRequest;
import com.seun.scheduler.dto.ScheduleResponse;
import com.seun.scheduler.repository.ScheduleRepository;
import com.seun.scheduler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Transactional
    public ScheduleResponse createPersonalSchedule(String userId, ScheduleRequest request) {

        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalArgumentException("유저를 찾을 수 없습니다.")
        );

        Schedule schedule = Schedule.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .location(request.getLocation())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .user(user)
                .build();

        scheduleRepository.save(schedule);

        return ScheduleResponse.of(schedule);
    }
}
