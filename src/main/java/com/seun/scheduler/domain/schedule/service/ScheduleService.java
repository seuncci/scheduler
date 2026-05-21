package com.seun.scheduler.domain.schedule.service;

import com.seun.scheduler.domain.group.entity.Group;
import com.seun.scheduler.domain.group.entity.GroupMemberStatus;
import com.seun.scheduler.domain.group.entity.GroupStatus;
import com.seun.scheduler.domain.group.repository.GroupMemberRepository;
import com.seun.scheduler.domain.group.repository.GroupRepository;
import com.seun.scheduler.domain.member.entity.Member;
import com.seun.scheduler.domain.member.repository.MemberRepository;
import com.seun.scheduler.domain.schedule.dto.ScheduleCreateRequest;
import com.seun.scheduler.domain.schedule.dto.ScheduleDetailResponse;
import com.seun.scheduler.domain.schedule.dto.ScheduleListResponse;
import com.seun.scheduler.domain.schedule.dto.ScheduleRangeRequest;
import com.seun.scheduler.domain.schedule.entity.Schedule;
import com.seun.scheduler.domain.schedule.repository.ScheduleRepository;
import com.seun.scheduler.global.common.ResultCode;
import com.seun.scheduler.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public void createSchedule(String memberId, ScheduleCreateRequest request) {

        if (request.getTargetType() == null) {

            throw new CustomException(ResultCode.SCHEDULE_TYPE_REQUIRED);
        }

        if ("GROUP".equals(request.getTargetType()) && request.getGroupId() == null) {

            throw new CustomException(ResultCode.GROUP_SELECTION_REQUIRED);
        }

        LocalDateTime startDateTime = Optional.ofNullable(request.getStartDateTime())
                .map(start -> start.length() <= 10 ?
                                LocalDate.parse(start).atStartOfDay() : LocalDateTime.parse(start)).orElse(null);
        LocalDateTime endDateTime = Optional.ofNullable(request.getEndDateTime())
                .map(end -> end.length() <= 10 ?
                        LocalDate.parse(end).atTime(23, 59, 59) : LocalDateTime.parse(end))
                .orElseThrow(() -> new CustomException(ResultCode.SCHEDULE_END_TIME_REQUIRED));

        if (startDateTime != null && endDateTime != null) {

            if (startDateTime.isAfter(endDateTime)) {

                throw new CustomException(ResultCode.INVALID_SCHEDULE_TIME);
            }
        }

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));
        Group group = request.getGroupId() == null ?
                null : groupRepository.findByIdAndStatus(request.getGroupId(), GroupStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.GROUP_NOT_FOUND));

        if (group != null) {

            groupMemberRepository.findByGroupAndMemberAndStatus(group, member, GroupMemberStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.NOT_GROUP_MEMBER));
        }

        scheduleRepository.save(Schedule.of(startDateTime, endDateTime, member, group, request));
    }

    public List<ScheduleListResponse> getSchedulesByRange(String memberId, ScheduleRangeRequest request) {

        LocalDateTime startDateTime = request.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = request.getEndDate().atTime(23, 59, 59);

        List<Schedule> privates = scheduleRepository.findPrivateSchedules(memberId, startDateTime, endDateTime);
        List<Schedule> groups = scheduleRepository.findGroupSchedules(memberId, startDateTime, endDateTime);

        return Stream.concat(privates.stream(), groups.stream())
                .distinct()
                .map(ScheduleListResponse::from)
                .sorted(Comparator.comparing(ScheduleListResponse::getStartDateTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    public ScheduleDetailResponse getScheduleDetail(String memberId, Long scheduleId) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));
        Schedule schedule = scheduleRepository.findWithGroupAndMemberById(scheduleId).orElseThrow(() -> new CustomException(ResultCode.SCHEDULE_NOT_FOUND));

        if (schedule.getGroup() == null) {

            if (!memberId.equals(schedule.getMember().getMemberId())) {

                throw new CustomException(ResultCode.ACCESS_DENIED_SCHEDULE);
            }
        } else {

            groupMemberRepository.findByGroupAndMemberAndStatus(schedule.getGroup(), member,
                    GroupMemberStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.NOT_GROUP_MEMBER));
        }

        return ScheduleDetailResponse.from(schedule);
    }

    /*

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
            if (!groupMemberRepository.existsByGroup_IdAndMember_MemberId(schedule.getGroup().getId(), userId)) {
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
            if (!groupMemberRepository.existsByGroup_IdAndMember_MemberId(schedule.getGroup().getId(), userId)) {
                throw new IllegalArgumentException("해당 그룹의 멤버만 일정을 조회할 수 있습니다.");
            }
        }

        return ScheduleDetailResponse.of(schedule);
    }

    */
}