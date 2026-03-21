package com.seun.scheduler.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse<T> {

    private int code;
    private String message;
    private T data;

    public static <T> CommonResponse<T> result(ResultCode code, T data) {

        return new CommonResponse<>(code.getCode(), code.getMessage(), data);
    }

    public static <T> CommonResponse<T> result(ResultCode code) {

        return new CommonResponse<>(code.getCode(), code.getMessage(), null);
    }
}