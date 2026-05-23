package com.seun.scheduler.domain.memo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemoCreateRequest {

    @NotBlank(message = "MEMO_CONTENT_REQUIRED")
    private String content;

    @NotBlank(message = "MEMO_CATEGORY_REQUIRED")
    private String category;
}