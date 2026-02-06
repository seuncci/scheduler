package com.seun.scheduler.service;

import com.seun.scheduler.domain.Group;
import com.seun.scheduler.domain.Schedule;
import com.seun.scheduler.domain.User;
import com.seun.scheduler.dto.ScheduleRequest;
import com.seun.scheduler.dto.ScheduleResponse;
import com.seun.scheduler.repository.GroupRepository;
import com.seun.scheduler.repository.GroupUserRepository;
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
    private final GroupRepository groupRepository;
    private final GroupUserRepository groupUserRepository;

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

        return ScheduleResponse.from(schedule, userId);
    }

    @Transactional
    public ScheduleResponse createGroupSchedule(long groupId, String userId, ScheduleRequest request) {

        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalArgumentException("유저를 찾을 수 없습니다.")
        );

        // 그룹이 존재하는지 확인
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("그룹 정보가 없습니다."));

        // 해당 유저가 그룹원의 멤버인지 확인
        if (!groupUserRepository.existsByGroup_IdAndUser_UserId(groupId, userId)) {
            throw new IllegalArgumentException("해당 그룹의 멤버만 일정을 등록할 수 있습니다.");
        }

        Schedule schedule = Schedule.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .location(request.getLocation())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .user(user)
                .group(group)
                .build();

        scheduleRepository.save(schedule);

        return ScheduleResponse.from(schedule, userId, groupId);
    }

    @Transactional
    public ScheduleResponse updateSchedule(long scheduleId, String userId, ScheduleRequest request) {
        Schedule schedule = scheduleRepository.findByIdWithUser(scheduleId).orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수가 없습니다."));

        // 본인이 작성한 일정인지 확인
        if (!schedule.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 일정만 수정이 가능합니다.");
        }

        schedule.update(request);

        // 개인 일정 등록일 경우
        if (schedule.getGroup() == null) return ScheduleResponse.from(schedule, userId);
        else return ScheduleResponse.from(schedule, userId, schedule.getGroup().getId());
    }

    @Transactional
    public void deleteSchedule(long scheduleId, String userId) {
        Schedule schedule = scheduleRepository.findByIdWithUser(scheduleId).orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수가 없습니다."));

        // 본인이 작성한 일정인지 확인
        if (!schedule.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 일정만 삭제가 가능합니다.");
        }

        scheduleRepository.delete(schedule);
    }
}
