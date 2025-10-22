package com.quyhoang.flexistudy.dto;

import com.quyhoang.flexistudy.entity.Job;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobMatchResult {
    Job job;
    double skillScore;
    boolean timeCompatible;
}
