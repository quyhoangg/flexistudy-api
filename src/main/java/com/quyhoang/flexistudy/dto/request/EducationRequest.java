package com.quyhoang.flexistudy.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EducationRequest {
    String userId;
    String school;
    String degree;
    String field;
}
