package com.seun.scheduler.domain.memo.repository;

import com.seun.scheduler.domain.memo.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    List<Memo> findByMemberMemberIdAndDeletedDateIsNullOrderByCreatedDateDesc(String memberId);

    Optional<Memo> findByIdAndDeletedDateIsNull(Long id);
}