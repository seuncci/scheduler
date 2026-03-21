package com.seun.scheduler.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberJoinRequest {

    @NotBlank(message = "EMPTY_MEMBER_ID")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]+$", message = "INVALID_MEMBER_ID_FORMAT")
    @Size(min = 4, max = 10, message = "INVALID_MEMBER_ID_SIZE")
    private String memberId;

    @NotBlank(message = "EMPTY_PASSWORD")
    @Pattern(regexp = "^(?=(.*[A-Za-z].*[0-9])|.*[A-Za-z].*[@$!%*#?&]|.*[0-9].*[@$!%*#?&])[A-Za-z0-9@$!%*#?&]{8,15}$", message = "INVALID_PASSWORD_FORMAT")
    private String password;

    @NotBlank(message = "EMPTY_PASSWORD_CONFIRM")
    private String passwordConfirm;

    @NotBlank(message = "EMPTY_NAME")
    private String name;

    @NotBlank(message = "EMPTY_EMAIL")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "INVALID_EMAIL_FORMAT")
    private String email;
}