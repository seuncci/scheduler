package com.seun.scheduler.domain.member.service;

import com.seun.scheduler.domain.group.entity.*;
import com.seun.scheduler.domain.group.repository.GroupInviteMemberRepository;
import com.seun.scheduler.domain.group.repository.GroupMemberRepository;
import com.seun.scheduler.domain.member.dto.*;
import com.seun.scheduler.domain.member.entity.Member;
import com.seun.scheduler.global.common.ResultCode;
import com.seun.scheduler.global.error.CustomException;
import com.seun.scheduler.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupInviteMemberRepository groupInviteMemberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(MemberJoinRequest request) {

        // 입력값 검증
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new CustomException(ResultCode.PASSWORD_MISMATCH);
        }

        if (isMemberIdDuplicated(request.getMemberId())) {
            throw new CustomException(ResultCode.DUPLICATE_MEMBER_ID);
        }

        if (isEmailDuplicated(request.getEmail())) {
            throw new CustomException(ResultCode.DUPLICATE_EMAIL);
        }

        memberRepository.save(Member.of(request, passwordEncoder.encode(request.getPassword())));
    }

    public MemberProfileResponse getMyProfile(String memberId) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));

        return MemberProfileResponse.from(member);
    }

    @Transactional
    public void updateProfile(String memberId, MemberProfileUpdateRequest request, MultipartFile profileImage) throws IOException {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));

       // 비밀번호 변경 시
       if (request.getPassword() != null) {
           if (!request.getPassword().equals(request.getPasswordConfirm())) {
               throw new CustomException(ResultCode.PASSWORD_MISMATCH);
           }

           String encodePassword = passwordEncoder.encode(request.getPassword());

           member.updatePassword(encodePassword);
       }

       // 이메일을 변경했을 경우만 중복 체크
        if (!member.getEmail().equals(request.getEmail()) && isEmailDuplicated(request.getEmail())) {
            throw new CustomException(ResultCode.DUPLICATE_EMAIL);
        }

       member.updateProfile(request.getName(), request.getEmail());

       if (profileImage != null && !profileImage.isEmpty()) {

           String fileName = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();
           String savePath = System.getProperty("user.dir") + "/src/main/resources/static/member/";

           if (member.getProfileImage() != null) {

               Path path = Paths.get(savePath + member.getProfileImage());

               Files.deleteIfExists(path);
           }

           profileImage.transferTo(new File(savePath + fileName));

           member.updateProfileImage(fileName);
       }
    }

    public NotificationSummaryResponse getNotificationSummary(String memberId) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));

        Page<GroupInviteMember> pages = groupInviteMemberRepository.findByMemberAndStatusAndGroup_Status(member, GroupInvitationStatus.PENDING, GroupStatus.ACTIVE,
                        PageRequest.of(0, 5, Sort.by(Sort.Order.desc("createdDate"))));

        return new NotificationSummaryResponse(pages.stream().map(GroupInvitationItem::from).toList(), pages.getTotalElements());
    }

    @Transactional
    public void acceptInvitation(String memberId, Long id) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));
        GroupInviteMember inviteMember = groupInviteMemberRepository.findByIdAndMember_MemberId(id, member).orElseThrow(() -> new CustomException(ResultCode.INVALID_INVITATION));

        if (inviteMember.getGroup().getStatus() != GroupStatus.ACTIVE) {

            throw new CustomException(ResultCode.INACTIVE_GROUP);
        }

        if (inviteMember.getStatus() != GroupInvitationStatus.PENDING) {

            throw new CustomException(ResultCode.ALREADY_PROCESSED_INVITE);
        }

        if (groupMemberRepository.findByGroupAndMemberAndStatus(inviteMember.getGroup(), member, GroupMemberStatus.ACTIVE).isPresent()) {

            throw new CustomException(ResultCode.ALREADY_GROUP_MEMBER);
        }

        inviteMember.updateStatus(GroupInvitationStatus.ACCEPTED);

        // 기존 가입 이력 조회
        Optional<GroupMember> history = groupMemberRepository.findByGroupAndMember(inviteMember.getGroup(), member);

        if (history.isPresent()) {

            GroupMember groupMember = history.get();

            groupMember.rejoin();

        } else {

            groupMemberRepository.save(GroupMember.of(inviteMember.getGroup(), member, GroupRole.MEMBER));
        }
    }

    @Transactional
    public void rejectInvitation(String memberId, Long id) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));
        GroupInviteMember inviteMember = groupInviteMemberRepository.findByIdAndMember_MemberId(id, member).orElseThrow(() -> new CustomException(ResultCode.INVALID_INVITATION));

        if (inviteMember.getGroup().getStatus() != GroupStatus.ACTIVE) {

            throw new CustomException(ResultCode.INACTIVE_GROUP);
        }

        if (inviteMember.getStatus() != GroupInvitationStatus.PENDING) {

            throw new CustomException(ResultCode.ALREADY_PROCESSED_INVITE);
        }

        if (groupMemberRepository.findByGroupAndMemberAndStatus(inviteMember.getGroup(), member, GroupMemberStatus.ACTIVE).isPresent()) {

            throw new CustomException(ResultCode.ALREADY_GROUP_MEMBER);
        }

        inviteMember.updateStatus(GroupInvitationStatus.REJECTED);
    }

    // 아이디 중복 체크
    private boolean isMemberIdDuplicated(String memberId) {

        return memberRepository.existsByMemberId(memberId);
    }

    // 이메일 중복 체크
    private boolean isEmailDuplicated(String email) {

        return memberRepository.existsByEmail(email);
    }
}