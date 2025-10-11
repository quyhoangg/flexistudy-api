package com.quyhoang.flexistudy.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobShiftUpdateRequest {
    @NotNull
    DayOfWeek dayOfWeek;

    @NotNull
    LocalTime startTime;

    @NotNull
    LocalTime endTime;

    String description;
}
