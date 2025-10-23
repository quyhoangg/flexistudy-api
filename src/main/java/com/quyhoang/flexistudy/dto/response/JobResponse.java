package com.quyhoang.flexistudy.dto.response;

import com.quyhoang.flexistudy.enums.EmployeeType;
import com.quyhoang.flexistudy.enums.JobCategory;
import com.quyhoang.flexistudy.enums.JobStatus;
import com.quyhoang.flexistudy.enums.WorkMode;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobResponse {
    String id;
    String title;

    String description;
    String requirements;
    String benefits;
    String address;
    LocalDateTime expiryDate;

    EmployeeType type;
    Integer minSalary;
    Integer maxSalary;
    String currency;
    JobCategory category;
    WorkMode mode;
    String city;
    JobStatus status;
    LocalDateTime postedAt;

    String companyName;
    String companyLogoUrl;
    List<JobShiftResponse> jobShifts;

    List<SkillResponse> requiredSkills;
    Integer quantity;
    Boolean isActive;
}

