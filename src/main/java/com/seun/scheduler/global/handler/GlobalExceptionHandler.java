package com.seun.scheduler.global.handler;

import com.seun.scheduler.global.common.CommonResponse;
import com.seun.scheduler.global.common.ResultCode;
import com.seun.scheduler.global.error.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Comparator;
import java.util.List;

@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonResponse<Void>> handleCustomException(CustomException e) {

        ResultCode code = e.getCode();

        return ResponseEntity
                .status(code.getStatus())
                .body(CommonResponse.result(code));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> handMethodArgumentValidException(MethodArgumentNotValidException e) {

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        ResultCode code = fieldErrors.stream()
                .map(error -> ResultCode.from(error.getDefaultMessage()))
                .min(Comparator.comparingInt(ResultCode::getCode))
                .orElse(ResultCode.UNKNOWN_ERROR);

        return ResponseEntity.status(code.getStatus()).body(CommonResponse.result(code));
    }
}