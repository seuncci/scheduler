package com.seun.scheduler.repository;

import com.seun.scheduler.domain.GroupUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {

}
