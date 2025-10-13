package com.quyhoang.flexistudy.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileResponse {
    String id;
    String firstName;
    String lastName;
    String email;
    String phone;
    String address;
    String avatarUrl;
    LocalDate dob;

    List<EducationResponse> educations;
    List<ExperienceResponse> experiences;
}
