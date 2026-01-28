package com.seun.scheduler.repository;

import com.seun.scheduler.domain.GroupUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {
    @Query("SELECT gu FROM GroupUser gu JOIN FETCH gu.group WHERE gu.user.userId = :userId")
    List<GroupUser> findAllByUserIdWithGroup(@Param("userId") String userId);
}
