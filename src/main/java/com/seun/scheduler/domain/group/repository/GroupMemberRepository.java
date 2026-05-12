package com.seun.scheduler.domain.group.repository;

import com.seun.scheduler.domain.group.dto.GroupMemberInfo;
import com.seun.scheduler.domain.group.dto.MyGroupResponse;
import com.seun.scheduler.domain.group.entity.*;
import com.seun.scheduler.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    @Query("SELECT new com.seun.scheduler.domain.group.dto.MyGroupResponse(" +
            "g.id, g.name, g.description, g.groupImage, " +
            "(SELECT COUNT(gm2) FROM GroupMember gm2 WHERE gm2.group = g AND gm2.status = :status)) " +
            "FROM GroupMember gm JOIN gm.group g " +
            "WHERE gm.member.memberId = :memberId AND gm.status = :status AND g.status = com.seun.scheduler.domain.group.entity.GroupStatus.ACTIVE")
    Page<MyGroupResponse> findAllMemberId(@Param("memberId") String memberId, @Param("status") GroupMemberStatus status, Pageable pageable);

    @Query("SELECT new com.seun.scheduler.domain.group.dto.GroupMemberInfo(m.memberId, m.name, m.email, m.profileImage, gm.role)" +
            " FROM GroupMember gm JOIN gm.member m WHERE gm.group.id = :groupId AND gm.status = :status")
    Page<GroupMemberInfo> findAllGroupId(@Param("groupId") Long groupId, @Param("status") GroupMemberStatus status, Pageable pageable);

    @Query("SELECT gm.role FROM GroupMember gm WHERE gm.group = :group AND gm.member.memberId = :memberId")
    GroupRole findByGroupAndMember_MemberId(@Param("group") Group group, @Param("memberId") String memberId);

    Optional<GroupMember> findByGroupAndMember(Group group, Member member);
    Optional<GroupMember> findByGroupAndMemberAndStatus(Group group, Member member, GroupMemberStatus status);

    Long countByGroupAndStatus(Group group, GroupMemberStatus status);
    Long countByMember_MemberIdAndGroup_Status(String memberId, GroupStatus status);

    Boolean existsByRoleAndGroupAndMember_MemberId(GroupRole role, Group group, String memberId);
}