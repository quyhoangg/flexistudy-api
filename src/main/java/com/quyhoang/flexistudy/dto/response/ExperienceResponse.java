package com.quyhoang.flexistudy.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExperienceResponse {
    UUID id;
    String company;
    String position;
    LocalDate startDate;
    LocalDate endDate;
}
