package com.seun.scheduler.domain.memo.service;

import com.seun.scheduler.domain.member.entity.Member;
import com.seun.scheduler.domain.member.repository.MemberRepository;
import com.seun.scheduler.domain.memo.dto.MemoCreateRequest;
import com.seun.scheduler.domain.memo.dto.MemoResponse;
import com.seun.scheduler.domain.memo.dto.MemoUpdateRequest;
import com.seun.scheduler.domain.memo.entity.Memo;
import com.seun.scheduler.domain.memo.repository.MemoRepository;
import com.seun.scheduler.global.common.ResultCode;
import com.seun.scheduler.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createMemo(String memberId, MemoCreateRequest request) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ResultCode.MEMBER_NOT_FOUND));

        memoRepository.save(Memo.of(member, request));
    }

    public List<MemoResponse> getMemos(String memberId) {

        return memoRepository.findByMemberMemberIdAndDeletedDateIsNullOrderByCreatedDateDesc(memberId).stream().map(MemoResponse::from).toList();
    }

    @Transactional
    public void updateMemo(String memberId, Long memoId, MemoUpdateRequest request) {

        Memo memo = memoRepository.findByIdAndDeletedDateIsNull(memoId).orElseThrow(() -> new CustomException(ResultCode.MEMO_NOT_FOUND));

        if (!memo.getMember().getMemberId().equals(memberId)) {

            throw new CustomException(ResultCode.ACCESS_DENIED_MEMO);
        }

        memo.update(request.getContent(), request.getCategory());
    }

    @Transactional
    public void deleteMemo(String memberId, Long memoId) {

        Memo memo = memoRepository.findByIdAndDeletedDateIsNull(memoId).orElseThrow(() -> new CustomException(ResultCode.MEMO_NOT_FOUND));

        if (!memo.getMember().getMemberId().equals(memberId)) {

            throw new CustomException(ResultCode.ACCESS_DENIED_MEMO);
        }

        memo.delete();
    }
}