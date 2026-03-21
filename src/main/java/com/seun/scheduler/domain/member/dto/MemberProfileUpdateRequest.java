package com.seun.scheduler.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberProfileUpdateRequest {

    @NotBlank(message = "EMPTY_NAME")
    private String name;

    @NotBlank(message = "EMPTY_EMAIL")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "INVALID_EMAIL_FORMAT")
    private String email;

    @Pattern(regexp = "^(?=(.*[A-Za-z].*[0-9])|.*[A-Za-z].*[@$!%*#?&]|.*[0-9].*[@$!%*#?&])[A-Za-z0-9@$!%*#?&]{8,15}$", message = "INVALID_PASSWORD_FORMAT")
    private String password;
    private String passwordConfirm;
}