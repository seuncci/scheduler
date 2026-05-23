package com.seun.scheduler.domain.schedule.service;

import com.seun.scheduler.domain.group.entity.Group;
import com.seun.scheduler.domain.group.entity.GroupMemberStatus;
import com.seun.scheduler.domain.group.entity.GroupStatus;
import com.seun.scheduler.domain.group.repository.GroupMemberRepository;
import com.seun.scheduler.domain.group.repository.GroupRepository;
import com.seun.scheduler.domain.member.entity.Member;
import com.seun.scheduler.domain.member.repository.MemberRepository;
import com.seun.scheduler.domain.schedule.dto.*;
import com.seun.scheduler.domain.schedule.entity.Schedule;
import com.seun.scheduler.domain.schedule.entity.ScheduleComment;
import com.seun.scheduler.domain.schedule.repository.ScheduleCommentRepository;
import com.seun.scheduler.domain.schedule.repository.ScheduleRepository;
import com.seun.scheduler.global.common.ResultCode;
import com.seun.scheduler.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final ScheduleCommentRepository scheduleCommentRepository;
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

        boolean isOwner = true;

        List<ScheduleCommentResponse> comments = List.of();

        long totalCount = 0L;

        if (schedule.getGroup() == null) {

            if (!memberId.equals(schedule.getMember().getMemberId())) {

                throw new CustomException(ResultCode.ACCESS_DENIED_SCHEDULE);
            }
        } else {

            groupMemberRepository.findByGroupAndMemberAndStatus(schedule.getGroup(), member,
                    GroupMemberStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.NOT_GROUP_MEMBER));

            if (!memberId.equals(schedule.getMember().getMemberId())) {

                isOwner = false;
            }

            Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdDate"));
            Page<ScheduleComment> commentPage = scheduleCommentRepository.findByScheduleIdAndDeletedDateIsNull(scheduleId, pageable);

            comments = commentPage.getContent().stream().map(comment -> ScheduleCommentResponse.of(comment, memberId)).toList();
            totalCount = commentPage.getTotalElements();
        }

        return ScheduleDetailResponse.from(schedule, isOwner, comments, totalCount);
    }

    @Transactional
    public void updateSchedule(String memberId, Long scheduleId, ScheduleUpdateRequest request) {

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
        Schedule schedule = scheduleRepository.findWithGroupAndMemberById(scheduleId).orElseThrow(() -> new CustomException(ResultCode.SCHEDULE_NOT_FOUND));

        if (!memberId.equals(schedule.getMember().getMemberId())) {

            throw new CustomException(ResultCode.UNAUTHORIZED_SCHEDULE_MODIFY);
        }

        if (schedule.getGroup() != null) {

            groupMemberRepository.findByGroupAndMemberAndStatus(schedule.getGroup(), member,
                    GroupMemberStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.LEFT_GROUP_SCHEDULE_MODIFY));
        }

        schedule.update(request, startDateTime, endDateTime);
    }

    @Transactional
    public void createComment(String memberId, Long scheduleId, ScheduleCommentCreateRequest request) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));
        Schedule schedule = scheduleRepository.findWithGroupAndMemberById(scheduleId).orElseThrow(() -> new CustomException(ResultCode.SCHEDULE_NOT_FOUND));

        if (schedule.getGroup() != null) {

            groupMemberRepository.findByGroupAndMemberAndStatus(schedule.getGroup(), member,
                    GroupMemberStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.NOT_GROUP_MEMBER));
        } else {

            throw new CustomException(ResultCode.CANNOT_COMMENT_ON_PERSONAL);
        }

        scheduleCommentRepository.save(ScheduleComment.builder()
                .content(request.getContent())
                .schedule(schedule)
                .member(member)
                .deletedDate(null)
                .build());
    }

    @Transactional(readOnly = true)
    public ScheduleCommentPageResponse getCommentPage(String memberId, Long scheduleId, Pageable pageable) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));
        Schedule schedule = scheduleRepository.findWithGroupAndMemberById(scheduleId).orElseThrow(() -> new CustomException(ResultCode.SCHEDULE_NOT_FOUND));

        if (schedule.getGroup() != null) {

            groupMemberRepository.findByGroupAndMemberAndStatus(schedule.getGroup(), member,
                    GroupMemberStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.NOT_GROUP_MEMBER));
        } else {

            throw new CustomException(ResultCode.CANNOT_COMMENT_ON_PERSONAL);
        }

        Page<ScheduleComment> commentPage = scheduleCommentRepository.findByScheduleIdAndDeletedDateIsNull(scheduleId, pageable);

        return ScheduleCommentPageResponse.of(commentPage, memberId);
    }

    @Transactional
    public void updateComment(String memberId, Long scheduleId, Long commentId, ScheduleCommentUpdateRequest request) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));
        Schedule schedule = scheduleRepository.findWithGroupAndMemberById(scheduleId).orElseThrow(() -> new CustomException(ResultCode.SCHEDULE_NOT_FOUND));
        ScheduleComment comment = scheduleCommentRepository.
                findByIdAndScheduleIdAndMemberMemberId(commentId, scheduleId, memberId).orElseThrow(() -> new CustomException(ResultCode.COMMENT_NOT_FOUND_OR_DENIED));

        if (schedule.getGroup() != null) {

            groupMemberRepository.findByGroupAndMemberAndStatus(schedule.getGroup(), member,
                    GroupMemberStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.NOT_GROUP_MEMBER));
        } else {

            throw new CustomException(ResultCode.CANNOT_COMMENT_ON_PERSONAL);
        }

        comment.updateContent(request.getContent());
    }

    @Transactional
    public void deleteComment(String memberId, Long scheduleId, Long commentId) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));
        Schedule schedule = scheduleRepository.findWithGroupAndMemberById(scheduleId).orElseThrow(() -> new CustomException(ResultCode.SCHEDULE_NOT_FOUND));
        ScheduleComment comment = scheduleCommentRepository.
                findByIdAndScheduleIdAndMemberMemberId(commentId, scheduleId, memberId).orElseThrow(() -> new CustomException(ResultCode.COMMENT_NOT_FOUND_OR_DENIED));

        if (schedule.getGroup() != null) {

            groupMemberRepository.findByGroupAndMemberAndStatus(schedule.getGroup(), member,
                    GroupMemberStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.NOT_GROUP_MEMBER));
        } else {

            throw new CustomException(ResultCode.CANNOT_COMMENT_ON_PERSONAL);
        }

        if (comment.isDeleted()) {

            throw new CustomException(ResultCode.COMMENT_ALREADY_DELETED);
        }

        comment.delete();
    }

    @Transactional
    public void deleteSchedule(String memberId, Long scheduleId) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));
        Schedule schedule = scheduleRepository.findWithGroupAndMemberById(scheduleId).orElseThrow(() -> new CustomException(ResultCode.SCHEDULE_NOT_FOUND));

        if (schedule.getGroup() != null) {

            groupMemberRepository.findByGroupAndMemberAndStatus(schedule.getGroup(), member,
                    GroupMemberStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.NOT_GROUP_MEMBER));
        }

        if (!memberId.equals(schedule.getMember().getMemberId())) {

            throw new CustomException(ResultCode.ACCESS_DENIED_SCHEDULE);
        }

        if (schedule.isDeleted()) {

            throw new CustomException(ResultCode.SCHEDULE_ALREADY_DELETED);
        }

        schedule.delete();
        scheduleCommentRepository.softDeleteAllByScheduleId(scheduleId, LocalDateTime.now());
    }
}