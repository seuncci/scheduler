package com.seun.scheduler.service;

import com.seun.scheduler.domain.Group;
import com.seun.scheduler.domain.Schedule;
import com.seun.scheduler.domain.ScheduleComment;
import com.seun.scheduler.domain.Member;
import com.seun.scheduler.dto.*;
import com.seun.scheduler.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ScheduleCommentRepository scheduleCommentRepository;
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final GroupUserRepository groupUserRepository;

    @Transactional
    public ScheduleResponse createPersonalSchedule(String userId, ScheduleRequest request) {

        Member member = memberRepository.findByMemberId(userId).orElseThrow(
                () -> new IllegalArgumentException("유저를 찾을 수 없습니다.")
        );

        Schedule schedule = Schedule.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .location(request.getLocation())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .member(member)
                .build();

        scheduleRepository.save(schedule);

        return ScheduleResponse.from(schedule, userId);
    }

    @Transactional
    public ScheduleResponse createGroupSchedule(long groupId, String userId, ScheduleRequest request) {

        Member member = memberRepository.findByMemberId(userId).orElseThrow(
                () -> new IllegalArgumentException("유저를 찾을 수 없습니다.")
        );

        // 그룹이 존재하는지 확인
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("그룹 정보가 없습니다."));

        // 해당 유저가 그룹원의 멤버인지 확인
        if (!groupUserRepository.existsByGroup_IdAndMember_MemberId(groupId, userId)) {
            throw new IllegalArgumentException("해당 그룹의 멤버만 일정을 등록할 수 있습니다.");
        }

        Schedule schedule = Schedule.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .location(request.getLocation())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .member(member)
                .group(group)
                .build();

        scheduleRepository.save(schedule);

        return ScheduleResponse.from(schedule, userId, groupId);
    }

    @Transactional
    public ScheduleResponse updateSchedule(long scheduleId, String userId, ScheduleRequest request) {
        Schedule schedule = scheduleRepository.findByIdWithUser(scheduleId).orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수가 없습니다."));

        // 본인이 작성한 일정인지 확인
        if (!schedule.getMember().getMemberId().equals(userId)) {
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
        if (!schedule.getMember().getMemberId().equals(userId)) {
            throw new IllegalArgumentException("본인의 일정만 삭제가 가능합니다.");
        }

        scheduleRepository.delete(schedule);
    }

    @Transactional
    public ScheduleCommentResponse createComment(long scheduleId, String userId, ScheduleCommentRequest request) {
        Member member = memberRepository.findByMemberId(userId).orElseThrow(
                () -> new IllegalArgumentException("유저를 찾을 수 없습니다.")
        );

        Schedule schedule = scheduleRepository.findByIdWithUser(scheduleId).orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수가 없습니다."));

        // 그룹 일정만 댓글 등록이 가능하기 때문에 그룹 일정인지 체크
        if (schedule.getGroup() != null) {
            // 해당 유저가 그룹원의 멤버인지 확인
            if (!groupUserRepository.existsByGroup_IdAndMember_MemberId(schedule.getGroup().getId(), userId)) {
                throw new IllegalArgumentException("해당 그룹의 멤버만 일정을 등록할 수 있습니다.");
            }
        }

        ScheduleComment comment = ScheduleComment.builder()
                .schedule(schedule)
                .member(member)
                .content(request.getContent())
                .build();

        scheduleCommentRepository.save(comment);

        return ScheduleCommentResponse.from(comment, schedule.getId(), userId);
    }

    @Transactional
    public ScheduleCommentResponse updateComment(long commentId, String userId, ScheduleCommentRequest request) {
        // 댓글이 등록 되어 있는 지 확인
        ScheduleComment comment = scheduleCommentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수가 없습니다."));

        // 일정이 등록 되어 있는 지 확인
        if (!scheduleRepository.existsById(comment.getSchedule().getId())) {
            throw new IllegalArgumentException("일정을 찾을 수가 없습니다.");
        }

        Schedule schedule = scheduleRepository.findByIdWithUser(comment.getSchedule().getId()).orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수가 없습니다."));

        comment.update(request.getContent());

        return ScheduleCommentResponse.from(comment, schedule.getId(), userId);
    }

    @Transactional
    public void deleteComment(long commentId, String userId) {
        // 댓글이 등록 되어 있는 지 확인
        ScheduleComment comment = scheduleCommentRepository.findByIdWithMember(commentId).orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수가 없습니다."));

        // 본인이 작성한 댓글인지 확인
        if (!comment.getMember().getMemberId().equals(userId)) {
            throw new IllegalArgumentException("본인의 댓글만 삭제가 가능합니다.");
        }

        scheduleCommentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public ScheduleDetailResponse getScheduleDetail(long scheduleId, String userId) {
        Schedule schedule = scheduleRepository.findByIdWithAll(scheduleId).orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수가 없습니다."));

        // 그룹 채팅인 경우 그룹원이 확인 가능하고 개인 일정이면 작성자가 확인 가능한지 확인
        if (schedule.getGroup() == null) {
            if (!schedule.getMember().getMemberId().equals(userId)) {
                throw new IllegalArgumentException("본인의 개인 일정만 조회할 수 있습니다.");
            }
        } else {
            if (!groupUserRepository.existsByGroup_IdAndMember_MemberId(schedule.getGroup().getId(), userId)) {
                throw new IllegalArgumentException("해당 그룹의 멤버만 일정을 조회할 수 있습니다.");
            }
        }

        return ScheduleDetailResponse.of(schedule);
    }
}
