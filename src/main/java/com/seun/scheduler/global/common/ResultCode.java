package com.seun.scheduler.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ResultCode {

    // 서버 관련 코드
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,5001, "알 수 없는 오류가 발생했습니다."),

    // 회원 관련 코드
    SIGNUP_SUCCESS(HttpStatus.CREATED, 2002, "회원가입이 완료되었습니다."),
    PROFILE_GET_SUCCESS(HttpStatus.OK, 2003, "프로필 정보를 성공적으로 불러왔습니다."),
    PROFILE_UPDATE_SUCCESS(HttpStatus.OK, 2004, "프로필 정보가 성공적으로 수정되었습니다."),

    EMPTY_MEMBER_ID(HttpStatus.BAD_REQUEST, 4001, "아이디를 입력하세요."),
    EMPTY_PASSWORD(HttpStatus.BAD_REQUEST, 4002, "비밀번호를 입력하세요."),
    EMPTY_PASSWORD_CONFIRM(HttpStatus.BAD_REQUEST, 4003, "비밀번호를 한 번 더 입력하세요"),
    EMPTY_NAME(HttpStatus.BAD_REQUEST, 4004, "이름을 입력하세요."),
    EMPTY_EMAIL(HttpStatus.BAD_REQUEST, 4005, "이메일을 입력하세요."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, 4006, "비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    DUPLICATE_MEMBER_ID(HttpStatus.CONFLICT, 4007, "이미 사용 중인 아이디입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, 4008, "이미 사용 중인 이메일입니다."),
    INVALID_MEMBER_ID_FORMAT(HttpStatus.BAD_REQUEST, 4009, "아이디는 영문과 숫자를 모두 포함해야 합니다."),
    INVALID_MEMBER_ID_SIZE(HttpStatus.BAD_REQUEST, 4010, "아이디는 최소 4자 최대 10자까지 입력해야 합니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, 4011, "비밀번호는 특수문자, 숫자, 영어 중에 최소 2종류 이상을 조합하여 8 ~ 15자로 입력해야 합니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, 4012, "이메일 형식이 올바르지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, 4013, "존재하지 않는 회원입니다.");

    private HttpStatus status;
    private int code;
    private String message;

    public static ResultCode from(String message) {

        try {
            return ResultCode.valueOf(message);
        } catch (Exception e) {
            return ResultCode.UNKNOWN_ERROR;
        }
    }
}