package com.seun.scheduler.repository;

import com.seun.scheduler.domain.GroupUser;
import com.seun.scheduler.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {
    @Query("SELECT gu FROM GroupUser gu JOIN FETCH gu.group WHERE gu.member.memberId = :memberId")
    List<GroupUser> findAllByMemberIdWithGroup(@Param("memberId") String userId);
    @Query("SELECT gu FROM GroupUser gu JOIN FETCH gu.member WHERE gu.group.id = :groupId")
    List<GroupUser> findAllByGroupIdWithUser(@Param("groupId") Long groupId);
    boolean existsByGroup_IdAndMember_MemberIdAndRole(Long groupId, String memberId, UserRole role);
    Optional<GroupUser> findByGroup_IdAndMember_MemberId(Long groupId, String memberId);
    long countByGroup_Id(Long groupId);
    Optional<GroupUser> findByGroup_IdAndMember_MemberIdAndRole(Long groupId, String memberId, UserRole role);
    boolean existsByGroup_IdAndMember_MemberId(Long groupId, String memberId);
}
