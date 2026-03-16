package com.seun.scheduler.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberJoinRequest {
    @NotBlank(message = "아이디를 입력하세요.")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "아이디는 영문, 숫자만 입력 가능합니다.")
    @Size(min = 4, max = 10, message = "아이디는 최소 4자 최대 10자까지 입력해야 합니다.")
    private String userId;

    @NotBlank(message = "비밀번호를 입력하세요.")
    @Pattern(regexp = "^(?=(.*[A-Za-z].*[0-9])|.*[A-Za-z].*[@$!%*#?&]|.*[0-9].*[@$!%*#?&])[A-Za-z0-9@$!%*#?&]{8,15}$", message = "비밀번호는 " +
            "특수문자, 숫자, 영어 중에 최소 2종류 이상을 조합하여 8 ~ 15자로 입력해야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호를 한 번 더 입력하세요")
    private String passwordConfirm;

    @NotBlank(message = "이름을 입력하세요.")
    private String name;

    @NotBlank(message = "이메일을 입력하세요.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
    private String email;
}
