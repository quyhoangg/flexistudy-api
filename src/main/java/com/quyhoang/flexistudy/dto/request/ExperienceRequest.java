package com.quyhoang.flexistudy.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExperienceRequest {
    String userId;
    String company;
    String position;
    LocalDate startDate;
    LocalDate endDate;
}
