package com.quyhoang.flexistudy.dto.response;

import com.quyhoang.flexistudy.enums.ApplicationStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationResponse {
    String id;
    String userId;
    String userFullName;
    String jobId;
    String jobTitle;
    ApplicationStatus status;
    String note;
    Instant appliedAt;
}
