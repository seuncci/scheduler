package com.seun.scheduler.domain.memo.controller;

import com.seun.scheduler.domain.memo.dto.MemoCreateRequest;
import com.seun.scheduler.domain.memo.dto.MemoResponse;
import com.seun.scheduler.domain.memo.dto.MemoUpdateRequest;
import com.seun.scheduler.domain.memo.service.MemoService;
import com.seun.scheduler.global.common.CommonResponse;
import com.seun.scheduler.global.common.ResultCode;
import com.seun.scheduler.security.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memos")
@RequiredArgsConstructor
public class MemoApiController {

    private final MemoService memoService;

    @PostMapping
    public CommonResponse<Void> createMemo(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid MemoCreateRequest request) {

        memoService.createMemo(userDetails.getUsername(), request);
        return CommonResponse.result(ResultCode.MEMO_CREATE_SUCCESS);
    }

    @GetMapping
    public CommonResponse<List<MemoResponse>> getMemos(@AuthenticationPrincipal CustomUserDetails userDetails) {

        return CommonResponse.result(ResultCode.MEMO_GET_SUCCESS, memoService.getMemos(userDetails.getUsername()));
    }

    @PutMapping("/{memoId}")
    public CommonResponse<Void> updateMemo(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("memoId") Long memoId,
                                           @RequestBody @Valid MemoUpdateRequest request) {

        memoService.updateMemo(userDetails.getUsername(), memoId, request);
        return CommonResponse.result(ResultCode.MEMO_UPDATE_SUCCESS);
    }

    @DeleteMapping("/{memoId}")
    public CommonResponse<Void> deleteMemo(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("memoId") Long memoId) {

        memoService.deleteMemo(userDetails.getUsername(), memoId);
        return CommonResponse.result(ResultCode.MEMO_DELETE_SUCCESS);
    }
}