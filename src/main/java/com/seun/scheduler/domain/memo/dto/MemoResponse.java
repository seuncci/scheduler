package com.seun.scheduler.domain.memo.dto;

import com.seun.scheduler.domain.memo.entity.Memo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemoResponse {

    private Long id;
    private String content;
    private String category;

    public static MemoResponse from(Memo memo) {

        return MemoResponse.builder()
                .id(memo.getId())
                .content(memo.getContent())
                .category(memo.getCategory())
                .build();
    }
}