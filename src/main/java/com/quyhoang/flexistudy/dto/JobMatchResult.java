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
    double skillScore;         // tỷ lệ kỹ năng trùng (0.0 – 1.0)
    boolean timeCompatible;    // có rảnh khớp thời gian không
}
