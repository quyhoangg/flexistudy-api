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
    String jobId;
    String jobTitle;
    String companyName;
    String userFullName;
    String jobLogoUrl;
    String city;
    String postedAt;
    String type;
    Integer minSalary;
    Integer maxSalary;

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
