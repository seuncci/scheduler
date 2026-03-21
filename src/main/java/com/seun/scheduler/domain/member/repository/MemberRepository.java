package com.seun.scheduler.domain.member.repository;

import com.seun.scheduler.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberId(String memberId);

    boolean existsByMemberId(String memberId);
    boolean existsByEmail(String email);
}
