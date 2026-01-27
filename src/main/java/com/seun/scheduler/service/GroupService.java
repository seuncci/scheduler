package com.seun.scheduler.service;

import com.seun.scheduler.domain.Group;
import com.seun.scheduler.domain.GroupUser;
import com.seun.scheduler.domain.User;
import com.seun.scheduler.domain.UserRole;
import com.seun.scheduler.dto.CommonResponse;
import com.seun.scheduler.dto.GroupCreateRequest;
import com.seun.scheduler.repository.GroupRepository;
import com.seun.scheduler.repository.GroupUserRepository;
import com.seun.scheduler.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
}
