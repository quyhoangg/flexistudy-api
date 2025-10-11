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
public class AvailabilityWindowResponse {
    String id;
    String userId;
    DayOfWeek dayOfWeek;
    LocalTime startTime;
    LocalTime endTime;
    String note;
}
