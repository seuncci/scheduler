package com.seun.scheduler.domain.group.repository;

import com.seun.scheduler.domain.group.entity.Group;
import com.seun.scheduler.domain.group.entity.GroupInvitationStatus;
import com.seun.scheduler.domain.group.entity.GroupInviteMember;
import com.seun.scheduler.domain.group.entity.GroupStatus;
import com.seun.scheduler.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupInviteMemberRepository extends JpaRepository<GroupInviteMember, Long> {

    @Query("SELECT gim FROM GroupInviteMember gim JOIN FETCH gim.group WHERE gim.member = :member AND gim.status = :status AND gim.group.status = :groupStatus")
    Page<GroupInviteMember> findByMemberAndStatusAndGroup_Status(Member member, @Param("status") GroupInvitationStatus status, @Param("groupStatus") GroupStatus groupStatus, Pageable pageable);

    @Query("SELECT gim FROM GroupInviteMember gim JOIN FETCH gim.group WHERE gim.id = :id AND gim.member = :member")
    Optional<GroupInviteMember> findByIdAndMember_MemberId(@Param("id") Long id, @Param("member") Member member);

    Boolean existsByGroupAndMemberAndStatus(Group group, Member member, GroupInvitationStatus status);
}