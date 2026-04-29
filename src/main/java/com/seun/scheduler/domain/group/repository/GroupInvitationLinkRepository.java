package com.seun.scheduler.domain.group.repository;

import com.seun.scheduler.domain.group.entity.Group;
import com.seun.scheduler.domain.group.entity.GroupInvitationLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GroupInvitationLinkRepository extends JpaRepository<GroupInvitationLink, Long> {

    List<GroupInvitationLink> findByGroupAndActiveTrueAndExpireDateAfterOrderByIdDesc(Group group, LocalDateTime now);
    Optional<GroupInvitationLink> findByIdAndGroupAndActiveTrue(Long linkId, Group group);

    @Query("SELECT gil FROM GroupInvitationLink gil JOIN FETCH gil.group WHERE gil.code = :code AND gil.active = true AND gil.expireDate > :now")
    Optional<GroupInvitationLink> findByCodeWithGroup(@Param("code") String code, @Param("now") LocalDateTime now);

    Long countByGroupAndActiveTrueAndExpireDateAfter(Group group, LocalDateTime now);

    Boolean existsByCode(String code);
}