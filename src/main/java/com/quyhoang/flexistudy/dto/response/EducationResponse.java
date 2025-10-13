package com.quyhoang.flexistudy.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EducationResponse {
    UUID id;
    String school;
    String degree;
    String field;
}
