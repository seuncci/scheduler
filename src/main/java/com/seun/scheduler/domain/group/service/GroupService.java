package com.seun.scheduler.domain.group.service;

import com.seun.scheduler.domain.group.config.GroupPolicy;
import com.seun.scheduler.domain.group.dto.*;
import com.seun.scheduler.domain.group.entity.*;
import com.seun.scheduler.domain.group.repository.GroupInvitationLinkRepository;
import com.seun.scheduler.domain.group.repository.GroupInviteMemberRepository;
import com.seun.scheduler.domain.member.entity.Member;
import com.seun.scheduler.domain.member.repository.MemberRepository;
import com.seun.scheduler.domain.schedule.dto.ScheduleListResponse;
import com.seun.scheduler.domain.schedule.dto.ScheduleRangeRequest;
import com.seun.scheduler.domain.schedule.entity.Schedule;
import com.seun.scheduler.domain.schedule.repository.ScheduleRepository;
import com.seun.scheduler.global.common.ResultCode;
import com.seun.scheduler.global.error.CustomException;
import com.seun.scheduler.domain.group.repository.GroupMemberRepository;
import com.seun.scheduler.domain.group.repository.GroupRepository;
import com.seun.scheduler.global.util.InvitationCodeGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupInvitationLinkRepository groupInvitationLinkRepository;
    private final GroupInviteMemberRepository groupInviteMemberRepository;
    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;

    public Page<MyGroupResponse> getMyGroupList (String memberId, Pageable pageable) {
        return groupMemberRepository.findAllMemberId(memberId, GroupMemberStatus.ACTIVE, pageable);
    }

    @Transactional
    public void createGroup(String memberId, GroupCreateRequest request, MultipartFile groupImage) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));
        Group group = Group.from(request);
        group.updateMember(member);

        try {
            if (groupImage != null && !groupImage.isEmpty()) {

                String fileName = UUID.randomUUID() + groupImage.getOriginalFilename();
                String savePath = System.getProperty("user.dir") + "/src/main/resources/static/group/";

                groupImage.transferTo(new File(savePath + fileName));
                group.updateGroupImage(fileName);
            }

        } catch (IOException e) {
            throw new CustomException(ResultCode.FILE_UPLOAD_FAILED);
        }

        groupRepository.save(group);
    }

    public GroupDetailResponse getGroup(Long groupId, String memberId) {

        Group group = groupRepository.findByIdAndStatus(groupId, GroupStatus.ACTIVE).orElseThrow(() -> new EntityNotFoundException(""));
        GroupRole role = groupMemberRepository.findByGroupAndMember_MemberId(group, memberId);
        Long memberCount = groupMemberRepository.countByGroupAndStatus(group, GroupMemberStatus.ACTIVE);
        return GroupDetailResponse.of(group, role, memberCount);
    }

    public Page<GroupMemberInfo> getGroupMembers(Long groupId, Pageable pageable) {

        Group group = groupRepository.findByIdAndStatus(groupId, GroupStatus.ACTIVE).orElseThrow(() -> new EntityNotFoundException(""));

        Sort defaultSort = Sort.by(Sort.Order.asc("role"));
        Sort sort = defaultSort.and(pageable.getSort().isSorted() ? pageable.getSort() : Sort.by(Sort.Order.desc("gm.lastJoinedDate")));

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return groupMemberRepository.findAllGroupId(groupId, GroupMemberStatus.ACTIVE, pageable);
    }

    @Transactional
    public void updateGroup(String memberId, GroupUpdateRequest request, MultipartFile groupImage, Long groupId) {

        Group group = groupRepository.findByIdAndStatus(groupId, GroupStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.GROUP_NOT_FOUND));

        // 수정하는 멤버가 관리자인지 확인
        if (!groupMemberRepository.existsByRoleAndGroupAndMember_MemberId(GroupRole.LEADER, group, memberId)) {

            throw new CustomException(ResultCode.NOT_GROUP_ADMIN);
        }

        group.updateGroupInfo(request.getName(), request.getDescription());

        try {
            if (groupImage != null && !groupImage.isEmpty()) {

                String fileName = UUID.randomUUID() + "_" + groupImage.getOriginalFilename();
                String savePath = System.getProperty("user.dir") + "/src/main/resources/static/group/";

                if (group.getGroupImage() != null) {

                    Path path = Paths.get(savePath + group.getGroupImage());
                    Files.deleteIfExists(path);
                }

                groupImage.transferTo(new File(savePath + fileName));
                group.updateGroupImage(fileName);
            }
        } catch (IOException e) {
            throw new CustomException(ResultCode.FILE_UPLOAD_FAILED);
        }
    }

    @Transactional
    public void createGroupInvitationLink(String memberId, Long expiredAt, Long groupId) {

        Group group = groupRepository.findByIdAndStatus(groupId, GroupStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.GROUP_NOT_FOUND));

        // 관리자 여부 확인
        if (!groupMemberRepository.existsByRoleAndGroupAndMember_MemberId(GroupRole.LEADER, group, memberId)) {

            throw new CustomException(ResultCode.NOT_GROUP_ADMIN);
        }

        if (groupInvitationLinkRepository.countByGroupAndActiveTrueAndExpireDateAfter(group, LocalDateTime.now()) >= GroupPolicy.MAX_INVITATION_LINK_COUNT) {

            throw new CustomException(ResultCode.EXCEED_INVITE_LINK_LIMIT);
        }

        String code;

        do {
            code = InvitationCodeGenerator.generate();

        } while (groupInvitationLinkRepository.existsByCode(code));

        LocalDateTime expireDate = LocalDate.now().plusDays(expiredAt).atTime(LocalTime.MAX);
        GroupInvitationLink invitationLink = GroupInvitationLink.builder()
                .group(group)
                .code(code)
                .expireDate(expireDate)
                .active(true)
                .build();

        groupInvitationLinkRepository.save(invitationLink);
    }

    public GroupInvitationSummaryResponse getInvitationLinks(String memberId, Long groupId) {

        Group group = groupRepository.findByIdAndStatus(groupId, GroupStatus.ACTIVE).orElseThrow(() -> new EntityNotFoundException(""));

        // 관리자 여부 확인
        if (!groupMemberRepository.existsByRoleAndGroupAndMember_MemberId(GroupRole.LEADER, group, memberId)) {

            throw new EntityNotFoundException("");
        }

        List<GroupInvitationLinkResponse> links = groupInvitationLinkRepository.findByGroupAndActiveTrueAndExpireDateAfterOrderByIdDesc(group, LocalDateTime.now())
                .stream().map(GroupInvitationLinkResponse::from).toList();

        return GroupInvitationSummaryResponse.form(links);
    }

    @Transactional
    public void deleteInvitationLink(String memberId, Long groupId, Long linkId) {

        Group group = groupRepository.findByIdAndStatus(groupId, GroupStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.GROUP_NOT_FOUND));

        // 관리자 여부 확인
        if (!groupMemberRepository.existsByRoleAndGroupAndMember_MemberId(GroupRole.LEADER, group, memberId)) {

            throw new CustomException(ResultCode.NOT_GROUP_ADMIN);
        }

        GroupInvitationLink link = groupInvitationLinkRepository.findByIdAndGroupAndActiveTrue(linkId, group).orElseThrow(() -> new CustomException(ResultCode.INVALID_INVITE_LINK));

        link.deleteCode();
    }

    public GroupInvitationResponse getGroupForInvitation(String code, String memberId) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));
        GroupInvitationLink link = groupInvitationLinkRepository.findByCodeWithGroup(code, LocalDateTime.now()).orElseThrow(() -> new CustomException(ResultCode.INVALID_INVITE_LINK));
        Group group = link.getGroup();

        if (group.getStatus() != GroupStatus.ACTIVE) {

            throw new CustomException(ResultCode.GROUP_NOT_FOUND);
        }

        // 기존 가입 이력 조회
        Optional<GroupMember> history = groupMemberRepository.findByGroupAndMember(group, member);

        if (history.isPresent()) {

            GroupMember groupMember = history.get();

            if (groupMember.getStatus() == GroupMemberStatus.ACTIVE) {

                throw new CustomException(ResultCode.ALREADY_GROUP_MEMBER);

            } else if (groupMember.getStatus() == GroupMemberStatus.KICKED) {

                throw new CustomException(ResultCode.BANNED_GROUP_MEMBER);
            }
        }

        Long memberCount = groupMemberRepository.countByGroupAndStatus(group, GroupMemberStatus.ACTIVE);

        return GroupInvitationResponse.of(group, memberCount);
    }

    @Transactional
    public void joinGroup(String code, String memberId) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));
        GroupInvitationLink link = groupInvitationLinkRepository.findByCodeWithGroup(code, LocalDateTime.now()).orElseThrow(() -> new CustomException(ResultCode.INVALID_INVITE_LINK));
        Group group = link.getGroup();

        if (group.getStatus() != GroupStatus.ACTIVE) {

            throw new CustomException(ResultCode.GROUP_NOT_FOUND);
        }

        // 기존 가입 이력 조회
        Optional<GroupMember> history = groupMemberRepository.findByGroupAndMember(group, member);

        if (history.isPresent()) {

            GroupMember groupMember = history.get();

            if (groupMember.getStatus() == GroupMemberStatus.ACTIVE) {

                throw new CustomException(ResultCode.ALREADY_GROUP_MEMBER);

            } else if (groupMember.getStatus() == GroupMemberStatus.KICKED) {

                throw new CustomException(ResultCode.BANNED_GROUP_MEMBER);
            }

            groupMember.rejoin();

        } else {
            groupMemberRepository.save(GroupMember.of(group, member, GroupRole.MEMBER));
        }
    }

    @Transactional
    public void kickMember(String adminMemberId, String kickId, Long groupId) {

        if (adminMemberId.equals(kickId)) {

            throw new CustomException(ResultCode.CANNOT_KICK_SELF);
        }

        Member kickMember = memberRepository.findByMemberId(kickId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));
        Group group = groupRepository.findByIdAndStatus(groupId, GroupStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.GROUP_NOT_FOUND));
        GroupMember groupMember = groupMemberRepository.findByGroupAndMemberAndStatus(group, kickMember, GroupMemberStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.NOT_GROUP_MEMBER));

        // 관리자 여부 확인
        if (!groupMemberRepository.existsByRoleAndGroupAndMember_MemberId(GroupRole.LEADER, group, adminMemberId)) {

            throw new CustomException(ResultCode.NOT_GROUP_ADMIN);
        }

        groupMember.kick();
    }

    @Transactional
    public void leaveGroup(String memberId, Long groupId) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));
        Group group = groupRepository.findByIdAndStatus(groupId, GroupStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.GROUP_NOT_FOUND));
        GroupMember groupMember =
                groupMemberRepository.findByGroupAndMemberAndStatus(group, member, GroupMemberStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.NOT_GROUP_MEMBER));

        // 관리자 여부 확인
        if (groupMemberRepository.existsByRoleAndGroupAndMember_MemberId(GroupRole.LEADER, group, memberId)) {

            throw new CustomException(ResultCode.ADMIN_CANNOT_LEAVE);
        }

        groupMember.leave();
    }

    @Transactional
    public void transferOwnership(String adminMemberId, String targetMemberId, Long groupId) {

        if (adminMemberId.equals(targetMemberId)) {

            throw new CustomException(ResultCode.CANNOT_DELEGATE_SELF);
        }

        Member adminMember = memberRepository.findByMemberId(adminMemberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));
        Member targetMember = memberRepository.findByMemberId(targetMemberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));
        Group group = groupRepository.findByIdAndStatus(groupId, GroupStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.GROUP_NOT_FOUND));

        GroupMember groupAdminMember =
                groupMemberRepository.findByGroupAndMemberAndStatus(group, adminMember, GroupMemberStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.NOT_GROUP_MEMBER));
        GroupMember groupTargetMember =
                groupMemberRepository.findByGroupAndMemberAndStatus(group, targetMember, GroupMemberStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.NOT_GROUP_MEMBER));

        // 관리자 여부 확인
        if (groupAdminMember.getRole() != GroupRole.LEADER) {

            throw new CustomException(ResultCode.NOT_GROUP_ADMIN);
        }

        groupAdminMember.delegate(GroupRole.MEMBER);
        groupTargetMember.delegate(GroupRole.LEADER);
    }

    @Transactional
    public void deleteGroup(String memberId, Long groupId) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));
        Group group = groupRepository.findByIdAndStatus(groupId, GroupStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.GROUP_NOT_FOUND));
        GroupMember groupMember =
                groupMemberRepository.findByGroupAndMemberAndStatus(group, member, GroupMemberStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.NOT_GROUP_MEMBER));

        // 관리자 여부 확인
        if (groupMember.getRole() != GroupRole.LEADER) {

            throw new CustomException(ResultCode.NOT_GROUP_ADMIN);
        }

        // 다른 멤버가 있다면 삭제 불가능
        if (groupMemberRepository.countByGroupAndStatus(group, GroupMemberStatus.ACTIVE) > 1) {

            throw new CustomException(ResultCode.GROUP_HAS_MEMBERS);
        }

        group.delete();
        groupMember.leave();
    }

    @Transactional
    public void inviteMember(String adminMemberId, String targetMemberId, Long groupId) {

        if (adminMemberId.equals(targetMemberId)) {

            throw new CustomException(ResultCode.CANNOT_INVITE_SELF);
        }

        Member targetMember = memberRepository.findByMemberId(targetMemberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));
        Group group = groupRepository.findByIdAndStatus(groupId, GroupStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.GROUP_NOT_FOUND));
        Optional<GroupMember> groupTargetMember = groupMemberRepository.findByGroupAndMemberAndStatus(group, targetMember, GroupMemberStatus.ACTIVE);

        // 관리자 여부 확인
        if (!groupMemberRepository.existsByRoleAndGroupAndMember_MemberId(GroupRole.LEADER, group, adminMemberId)) {

            throw new CustomException(ResultCode.NOT_GROUP_ADMIN);
        }

        if (groupTargetMember.isPresent()) {

            throw new CustomException(ResultCode.ALREADY_GROUP_MEMBER);
        }

        if (groupInviteMemberRepository.existsByGroupAndMemberAndStatus(group, targetMember, GroupInvitationStatus.PENDING)) {

            throw new CustomException(ResultCode.ALREADY_INVITED_MEMBER);
        }

        groupInviteMemberRepository.save(GroupInviteMember.of(group, targetMember));
    }

    public Long getMyGroupCount(String memberId) {

        return groupMemberRepository.countByMember_MemberIdAndGroup_Status(memberId, GroupStatus.ACTIVE);
    }

    public List<ScheduleListResponse> getGroupCalendarSchedules(String memberId, Long groupId, ScheduleRangeRequest request) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));
        Group group = groupRepository.findByIdAndStatus(groupId, GroupStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.GROUP_NOT_FOUND));

        groupMemberRepository.findByGroupAndMemberAndStatus(group, member,
                GroupMemberStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.NOT_GROUP_MEMBER));

        LocalDateTime startDateTime = request.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = request.getEndDate().atTime(23, 59, 59);

        List<Schedule> schedules = scheduleRepository.findSchedulesByGroupIdAndPeriod(groupId, startDateTime, endDateTime);

        return schedules.stream().map(ScheduleListResponse::from).toList();
    }

    public List<ScheduleListResponse> getUpcomingGroupSchedules(String memberId, Long groupId) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));
        Group group = groupRepository.findByIdAndStatus(groupId, GroupStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.GROUP_NOT_FOUND));

        groupMemberRepository.findByGroupAndMemberAndStatus(group, member,
                GroupMemberStatus.ACTIVE).orElseThrow(() -> new CustomException(ResultCode.NOT_GROUP_MEMBER));

        LocalDateTime tomorrowStart = LocalDateTime.now().plusDays(1).toLocalDate().atStartOfDay();
        LocalDateTime weekEnd = LocalDateTime.now().plusDays(7).toLocalDate().atTime(23, 59, 59);

        List<Schedule> schedules = scheduleRepository.findUpComingSchedules(groupId, tomorrowStart, weekEnd, PageRequest.of(0, 3));

        return schedules.stream().map(ScheduleListResponse::from).toList();
    }
}