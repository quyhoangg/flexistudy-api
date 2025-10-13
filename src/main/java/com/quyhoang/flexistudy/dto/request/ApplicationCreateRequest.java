package com.quyhoang.flexistudy.dto.request;

import com.quyhoang.flexistudy.enums.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationCreateRequest {
    String id;
    String jobId;
    String jobTitle;
    String companyName;

    String userId;
    String fullName;
    String email;
    String phone;

    String cvUrl;
    String coverLetter;

    ApplicationStatus status;
    Instant appliedAt;
    Instant updatedAt;
    String note;
}
