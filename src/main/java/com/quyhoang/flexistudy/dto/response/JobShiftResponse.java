package com.quyhoang.flexistudy.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobShiftResponse {
    String id;
    String jobId;
    DayOfWeek dayOfWeek;
    LocalTime startTime;
    LocalTime endTime;
    String description;
}
