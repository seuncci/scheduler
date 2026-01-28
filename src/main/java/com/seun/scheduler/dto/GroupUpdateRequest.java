package com.seun.scheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class GroupUpdateRequest {
    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 2, max = 15, message = "이름은 2 ~ 15자 사이여야 합니다.")
    private String name;
}
