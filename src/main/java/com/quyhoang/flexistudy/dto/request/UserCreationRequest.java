package com.quyhoang.flexistudy.dto.request;

import com.quyhoang.flexistudy.enums.RoleName;
import com.quyhoang.flexistudy.validator.DobConstraint;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    @Size(min = 4, message = "USERNAME_INVALID")
    String username;
    String email;
    @Size(min = 9, message = "PASSWORD_INVALID")
    String password;
    String firstName;
    String lastName;
    String address;
    String phone;
    String avatarUrl;
    List<RoleName> roles;

    @DobConstraint(min = 12, message = "INVALID_DOB")
    LocalDate dob;
}
