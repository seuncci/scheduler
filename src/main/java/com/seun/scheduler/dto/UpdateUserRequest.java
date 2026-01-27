package com.seun.scheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UpdateUserRequest {
    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수입니다.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Pattern(regexp = "^(?=(.*[A-Za-z].*[0-9])|.*[A-Za-z].*[@$!%*#?&]|.*[0-9].*[@$!%*#?&])[A-Za-z0-9@$!%*#?&]{8,15}$", message = "비밀번호는 " +
            "특수문자, 숫자, 영어 중에 최소 2종류 이상을 조합하여 8 ~ 15자로 입력해야 합니다.")
    private String password;
    private String passwordConfirm;
}
