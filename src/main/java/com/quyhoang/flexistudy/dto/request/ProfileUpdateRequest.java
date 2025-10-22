package com.quyhoang.flexistudy.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileUpdateRequest {
    String firstName;
    String lastName;
    String email;
    String phone;
    String address;
    String avatarUrl;
    LocalDate dob;

    List<EducationRequest> educations;
    List<ExperienceRequest> experiences;

    List<String> skillIds;
}
