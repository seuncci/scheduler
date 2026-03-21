package com.seun.scheduler.global.error;

import com.seun.scheduler.global.common.ResultCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ResultCode code;

    public CustomException(ResultCode code) {

        super(code.getMessage());

        this.code = code;
    }
}