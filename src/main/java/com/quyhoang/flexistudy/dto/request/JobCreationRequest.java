package com.quyhoang.flexistudy.dto.request;

import com.quyhoang.flexistudy.enums.EmployeeType;
import com.quyhoang.flexistudy.enums.JobCategory;
import com.quyhoang.flexistudy.enums.JobStatus;
import com.quyhoang.flexistudy.enums.WorkMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobCreationRequest {
    @NotBlank
    String title;

    String description;
    String requirements;
    String benefits;
    String address;
    LocalDateTime expiryDate;

    @NotNull
    EmployeeType type;

    Integer minSalary;
    Integer maxSalary;
    @NotBlank
    String currency;

    @NotNull
    JobCategory category;

    @NotNull
    WorkMode mode;

    @NotBlank
    String city;

    @NotBlank
    String companyId;

    @NotEmpty
    Set<String> skillIds;
}

