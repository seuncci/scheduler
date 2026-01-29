package com.seun.scheduler.repository;

import com.seun.scheduler.domain.GroupInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long> {

}
