package com.seun.scheduler.service;

import com.seun.scheduler.domain.Group;
import com.seun.scheduler.domain.GroupUser;
import com.seun.scheduler.domain.User;
import com.seun.scheduler.domain.UserRole;
import com.seun.scheduler.dto.*;
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
        if (!groupUserRepository.existsByGroup_IdAndUser_UserIdAndRole(groupId, userId, UserRole.LEADER)) {
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
        if (!groupUserRepository.existsByGroup_IdAndUser_UserIdAndRole(groupId, userId, UserRole.LEADER)) {
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
}
