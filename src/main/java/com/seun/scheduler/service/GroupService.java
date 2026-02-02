package com.seun.scheduler.service;

import com.seun.scheduler.domain.*;
import com.seun.scheduler.dto.*;
import com.seun.scheduler.repository.GroupInvitationRepository;
import com.seun.scheduler.repository.GroupRepository;
import com.seun.scheduler.repository.GroupUserRepository;
import com.seun.scheduler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupUserRepository groupUserRepository;
    private final GroupInvitationRepository groupInvitationRepository;
    private final NotificationService notificationService;

    @Transactional
    public ResponseEntity<CommonResponse<Void>> create (
            String userId, GroupCreateRequest request, MultipartFile image
    ) throws IOException {
        String fileName = null;

        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalArgumentException("유저를 찾을 수 없습니다.")
        );

        if (image != null && !image.isEmpty()) {
            fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            String savePath = System.getProperty("user.dir") + "/src/main/resources/static/group/";
            image.transferTo(new File(savePath + fileName));
        }

        Group group = Group.builder()
                .name(request.getName())
                .groupImage(fileName)
                .build();

        groupRepository.save(group);

        GroupUser groupUser = GroupUser.builder()
                .group(group)
                .user(user)
                .role(UserRole.LEADER)
                .build();

        groupUserRepository.save(groupUser);

        CommonResponse<Void> response = CommonResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK.name())
                .message("success")
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<CommonResponse<List<GroupResponse>>> getMyGroupList (String userId) {

        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalArgumentException("유저를 찾을 수 없습니다.")
        );

        List<GroupUser> groups = groupUserRepository.findAllByUserIdWithGroup(userId);
        List<GroupResponse> responses = new ArrayList<>();

        for (GroupUser gu : groups) {
            responses.add(
                GroupResponse.builder()
                        .groupId(gu.getGroup().getId())
                        .groupImage(gu.getGroup().getGroupImage())
                        .build()
            );

        }

        CommonResponse<List<GroupResponse>> response = CommonResponse.<List<GroupResponse>>builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK.name())
                .message("조회 성공")
                .data(responses)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Transactional(readOnly = true)
    public GroupDetailResponse getGroupDetail(long groupId) {
        // 그룹이 존재하는지 확인 및 그룹 정보 가져오기
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("그룹 정보가 없습니다."));
        // 그룹원 정보 가져오기
        List<GroupUser> groupUsers = groupUserRepository.findAllByGroupIdWithUser(groupId);
        List<UserResponse> users = new ArrayList<>();

        for (GroupUser user : groupUsers) {
            users.add(
                    UserResponse.builder()
                            .userId(user.getUser().getUserId())
                            .name(user.getUser().getName())
                            .profileImage(user.getUser().getProfileImage())
                            .role(user.getRole())
                            .build()
            );
        }

        return GroupDetailResponse.builder()
                .name(group.getName())
                .groupImage(group.getGroupImage())
                .users(users)
                .build();
    }

    @Transactional
    public GroupUpdateResponse updateGroup (String userId, Long groupId, GroupUpdateRequest request, MultipartFile image) throws IOException {
        // 그룹이 존재하는지 확인 및 그룹 정보 가져오기
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("그룹 정보가 없습니다."));

        // 그룹장 여부 확인
        if (groupUserRepository.existsByGroup_IdAndUser_UserIdAndRole(groupId, userId, UserRole.LEADER)) {
            throw new IllegalArgumentException("그룹 정보 수정 권한이 없습니다.");
        }

        if (image != null && !image.isEmpty()) {

            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            String savePath = System.getProperty("user.dir") + "/src/main/resources/static/group/";

            // 이미 저장된 이미지가 있을 경우 이미지 삭제
            if (group.getGroupImage() != null) {
                Path filePath = Paths.get(savePath + group.getGroupImage());
                Files.deleteIfExists(filePath);
            }
            // file upload
            image.transferTo(new File(savePath + fileName));
            group.updateImage(fileName);
        }

        group.updateName(request.getName());

        // 그룹원 정보 가져오기
        List<GroupUser> groupUsers = groupUserRepository.findAllByGroupIdWithUser(groupId);
        List<UserResponse> responses = new ArrayList<>();

        for (GroupUser gu : groupUsers) {
            responses.add(UserResponse.of(gu));
        }

        return GroupUpdateResponse.from(group, responses);
    }

    @Transactional
    public void leaveGroup(Long groupId, String userId) throws IOException {
        // 그룹이 존재하는지 확인 및 그룹 정보 가져오기
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("그룹 정보가 없습니다."));

        // 그룹원 여부 확인
        GroupUser groupUser = groupUserRepository.findByGroup_IdAndUser_UserId(groupId, userId).orElseThrow(() -> new IllegalArgumentException("해당 그룹원이 아닙니다."));

        /*
            그룹원 권한 확인
            USER - 탈퇴 가능
            LEADER - 탈퇴 불가. 그룹원이 자기 자신 밖에 없을 경우 그룹 삭제
         */
        if (groupUser.getRole() == UserRole.USER) {
            groupUserRepository.delete(groupUser);
        } else if (groupUser.getRole() == UserRole.LEADER) {

            if (groupUserRepository.countByGroup_Id(groupId) == 1) {
                // 이미지 삭제 후 그룹 삭제
                String savePath = System.getProperty("user.dir") + "/src/main/resources/static/group/";

                if (group.getGroupImage() != null) {
                    Path filePath = Paths.get(savePath + group.getGroupImage());
                    Files.deleteIfExists(filePath);
                }

                groupRepository.delete(group);
            } else {
                throw new IllegalArgumentException("그룹원이 있을 경우 그룹장을 위임한 후에 탈퇴가 가능합니다.");
            }
        }
    }

    @Transactional
    public void deleteGroup(Long groupId, String userId) throws IOException {
        // 그룹이 존재하는지 확인 및 그룹 정보 가져오기
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("그룹 정보가 없습니다."));

        // 그룹장 여부 확인
        if (groupUserRepository.existsByGroup_IdAndUser_UserIdAndRole(groupId, userId, UserRole.LEADER)) {
            throw new IllegalArgumentException("그룹 삭제 권한이 없습니다.");
        } else {
            // 이미지 삭제 후 그룹 삭제
            String savePath = System.getProperty("user.dir") + "/src/main/resources/static/group/";

            if (group.getGroupImage() != null) {
                Path filePath = Paths.get(savePath + group.getGroupImage());
                Files.deleteIfExists(filePath);
            }

            groupRepository.delete(group);
        }
    }

    @Transactional
    public void delegateLeader(Long groupId, String currentLeaderId, String targetMemberId) {
        // 자기 자신에게 위임 x
        if (currentLeaderId.equals(targetMemberId)) {
            throw new IllegalArgumentException("자기 자신에게 위임할 수 없습니다.");
        }

        // 그룹 존재 여부 확인
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("그룹 정보가 없습니다."));
        // 그룹장 여부 확인 ( 위임 전 그룹장 )
        GroupUser curLeader = groupUserRepository.findByGroup_IdAndUser_UserIdAndRole(groupId, currentLeaderId, UserRole.LEADER)
                .orElseThrow(() -> new IllegalArgumentException("그룹장만 위임이 가능합니다."));
        // 위임할 멤버가 그룹원인지 확인
        GroupUser targetMember = groupUserRepository.findByGroup_IdAndUser_UserIdAndRole(groupId, targetMemberId, UserRole.USER)
                .orElseThrow(() -> new IllegalArgumentException("그룹원만 위임을 받을 수 있습니다."));

        // 그룹장 위임
        curLeader.updateRole(UserRole.USER);
        targetMember.updateRole(UserRole.LEADER);
    }

    @Transactional
    public void inviteMember(Long groupId, String leaderId, String targetMemberId) {
        // 그룹 존재 여부 확인
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("그룹 정보가 없습니다."));

        // 초대자와 초대 받는 멤버 정보
        User inviter = userRepository.findByUserId(leaderId).orElseThrow(() -> new IllegalArgumentException("해당 유저의 정보가 없습니다."));
        User invitee  = userRepository.findByUserId(targetMemberId).orElseThrow(() -> new IllegalArgumentException("해당 유저의 정보가 없습니다."));

        // 초대자가 그룹장인지 확인
        if (!groupUserRepository.existsByGroup_IdAndUser_UserIdAndRole(groupId, leaderId, UserRole.LEADER)) {
            throw new IllegalArgumentException("해당 그룹의 그룹장만 초대가 가능합니다");
        }

        // 초대 받는 멤버가 이미 그룹에 있는지 확인
        if (groupUserRepository.existsByGroup_IdAndUser_UserId(groupId, targetMemberId)) {
            throw new IllegalArgumentException("이미 해당 그룹에 속해 있습니다.");
        }

        // 초대 데이터 추가
        GroupInvitation invitation = GroupInvitation.builder()
                .group(group)
                .inviter(inviter)
                .invitee(invitee)
                .status(InvitationStatus.PENDING)
                .build();

        groupInvitationRepository.save(invitation);

        // 실시간 알림
        notificationService.send(targetMemberId, group.getName() + " 그룹에서 초대하셨습니다.");
    }

    @Transactional
    public void processInvitation(Long invitationId, String userId, InvitationStatus status) {
        // 초대 존재 여부 확인
        GroupInvitation invitation = groupInvitationRepository.findById(invitationId).orElseThrow(
                () -> new IllegalArgumentException("초대 정보가 존재하지 않습니다.")
        );

        // 본인에게 온 초대인지 확인
        if (!invitation.getInvitee().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인에게 온 초대만 처리가 가능합니다.");
        }

        // 이미 처리된 초대인지 확인
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 초대입니다.");
        }

        invitation.updateStatus(status);

        // 수락 시 그룹원으로 추가
        if (invitation.getStatus() == InvitationStatus.ACCEPTED) {

            GroupUser newMember = GroupUser.builder()
                    .group(invitation.getGroup())
                    .user(invitation.getInvitee())
                    .role(UserRole.USER)
                    .build();

            groupUserRepository.save(newMember);
        }
    }

    @Transactional(readOnly = true)
    public List<GroupInvitationResponse> getMyInvitations(String userId) {
        List<GroupInvitation> invitations = groupInvitationRepository.findAllByInviteeIdAndPending(userId);
        List<GroupInvitationResponse> responses = new ArrayList<>();

        for (GroupInvitation gi : invitations) {
            responses.add(GroupInvitationResponse.of(gi));
        }

        return responses;
    }
}
