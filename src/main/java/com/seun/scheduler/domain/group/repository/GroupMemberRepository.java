package com.seun.scheduler.domain.group.repository;

import com.seun.scheduler.domain.group.dto.GroupMemberInfo;
import com.seun.scheduler.domain.group.dto.MyGroupResponse;
import com.seun.scheduler.domain.group.entity.GroupMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    @Query("SELECT new com.seun.scheduler.domain.group.dto.MyGroupResponse(g.id, g.name, g.description, g.groupImage, (SELECT COUNT(gm2) FROM GroupMember gm2 WHERE gm2.group = g))" +
            "FROM GroupMember gm JOIN gm.group g WHERE gm.member.memberId = :memberId")
    Page<MyGroupResponse> findAllMemberId(String memberId, Pageable pageable);

    @Query("SELECT new com.seun.scheduler.domain.group.dto.GroupMemberInfo(m.memberId, m.name, m.email, m.profileImage, gm.role)" +
            " FROM GroupMember gm JOIN gm.member m WHERE gm.group.id = :groupId")
    Page<GroupMemberInfo> findAllGroupId(@Param("groupId") Long groupId, Pageable pageable);
}