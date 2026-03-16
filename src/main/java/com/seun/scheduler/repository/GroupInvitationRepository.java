package com.seun.scheduler.repository;

import com.seun.scheduler.domain.GroupInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long> {
    @Query("SELECT gi FROM GroupInvitation gi JOIN FETCH gi.group JOIN FETCH gi.inviter WHERE gi.invitee.memberId = :inviteeId AND gi.status = 'PENDING'")
    List<GroupInvitation> findAllByInviteeIdAndPending(@Param("inviteeId") String inviteeId);
}
