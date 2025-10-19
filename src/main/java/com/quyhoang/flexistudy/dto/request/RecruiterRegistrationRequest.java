package com.quyhoang.flexistudy.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecruiterRegistrationRequest {
    @NotBlank
    @Email
    String email;

    @NotBlank
    String password;

    @NotBlank
    String firstName;

    @NotBlank
    String lastName;

    // Thông tin công ty
    @NotBlank
    String companyName;

    String description;
    String website;
    Integer memberNumber;
}
