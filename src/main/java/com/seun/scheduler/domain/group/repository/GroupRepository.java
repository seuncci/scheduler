package com.seun.scheduler.domain.group.repository;

import com.seun.scheduler.domain.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}