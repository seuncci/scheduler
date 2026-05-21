package com.seun.scheduler.domain.schedule.dto;

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
public class ScheduleUpdateRequest {

    @NotBlank(message = "SCHEDULE_TITLE_REQUIRED")
    private String title;

    private String content;
    private String location;

    @NotBlank(message = "SCHEDULE_COLOR_REQUIRED")
    private String color;

    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])(T([01][0-9]|2[0-3]):([0-5][0-9]))?$",
            message = "SCHEDULE_DATE_REQUIRED")
    private String startDateTime;

    @NotBlank(message = "SCHEDULE_END_TIME_REQUIRED")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])(T([01][0-9]|2[0-3]):([0-5][0-9]))?$",
            message = "SCHEDULE_DATE_REQUIRED")
    private String endDateTime;

    @Builder.Default
    private Boolean isCompleted = false;
}