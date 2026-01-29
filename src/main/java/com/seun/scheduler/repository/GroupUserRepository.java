package com.seun.scheduler.repository;

import com.seun.scheduler.domain.GroupUser;
import com.seun.scheduler.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {
    @Query("SELECT gu FROM GroupUser gu JOIN FETCH gu.group WHERE gu.user.userId = :userId")
    List<GroupUser> findAllByUserIdWithGroup(@Param("userId") String userId);
    @Query("SELECT gu FROM GroupUser gu JOIN FETCH gu.user WHERE gu.group.id = :groupId")
    List<GroupUser> findAllByGroupIdWithUser(@Param("groupId") Long groupId);
    boolean existsByGroup_IdAndUser_UserIdAndRole(Long groupId, String userId, UserRole role);
    Optional<GroupUser> findByGroup_IdAndUser_UserId(Long groupId, String userId);
    long countByGroup_Id(Long groupId);
}
