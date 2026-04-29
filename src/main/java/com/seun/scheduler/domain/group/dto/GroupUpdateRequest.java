package com.seun.scheduler.domain.group.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupUpdateRequest {

    @NotBlank(message = "EMPTY_NAME")
    @Size(min = 2, max = 15, message = "INVALID_NAME_FORMAT")
    private String name;

    private String description;
}